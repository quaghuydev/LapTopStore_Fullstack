package vn.id.quanghuydevfs.drcomputer.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import vn.id.quanghuydevfs.drcomputer.controller.auth.AuthenticationResponse;
import vn.id.quanghuydevfs.drcomputer.dto.auth.AuthenticationDto;
import vn.id.quanghuydevfs.drcomputer.dto.auth.RegisterDto;

import vn.id.quanghuydevfs.drcomputer.dto.user.UserDto;
import vn.id.quanghuydevfs.drcomputer.model.token.Token;
import vn.id.quanghuydevfs.drcomputer.model.token.TokenRepository;
import vn.id.quanghuydevfs.drcomputer.model.token.TokenType;
import vn.id.quanghuydevfs.drcomputer.model.user.Roles;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.UserRepository;
import vn.id.quanghuydevfs.drcomputer.security.jwt.JwtService;
import vn.id.quanghuydevfs.drcomputer.util.payment.vnpay.Config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService implements LogoutHandler {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AuthenticationResponse register(RegisterDto request) {
        var user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .isEnabled(true)
                .createdAt(LocalDate.now())
                .rewardPoints(0)
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        var userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setFullname(user.getFullname());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRoles(user.getRoles());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        var userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setFullname(user.getFullname());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRoles(user.getRoles());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }
    public AuthenticationResponse authGoogle(UserDto uDto) {
        var user = repository.findByEmail(uDto.getEmail());
        if (user.isEmpty()){
            RegisterDto u = RegisterDto.builder()
                    .fullname(uDto.getFullname())
                    .email(uDto.getEmail())
                    .password(Config.getRandomNumber(9))
                    .isEnabled(true)
                    .role(Roles.USER)
                    .build();
            return register(u);
        }else{
            var jwtToken = jwtService.generateToken(user.get());
            var refreshToken = jwtService.generateRefreshToken(user.get());
            revokeAllUserTokens(user.get());
            saveUserToken(user.get(), jwtToken);

            var userDto = new UserDto();
            userDto.setEmail(user.get().getEmail());
            userDto.setFullname(user.get().getFullname());
            userDto.setPhoneNumber(user.get().getPhoneNumber());
            userDto.setRoles(user.get().getRoles());

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(userDto)
                    .build();

        }

    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }

    public void updateResetPasswordToken(String token, String email)   {
        User customer = repository.findBEmail(email);
        if (customer != null) {
            customer.setResetPasswordToken(token);
            repository.save(customer);
            scheduleTokenReset(customer, 10, TimeUnit.MINUTES);
        } else {
            System.out.println("Could not find any customer with the email " + email );
        }
    }
    private void scheduleTokenReset(User customer, long delay, TimeUnit unit) {
        scheduler.schedule(() -> resetToken(customer), delay, unit);
    }

    public User getByResetPasswordToken(String token) {
        return repository.findByResetPasswordToken(token);
    }
    private void resetToken(User customer) {
        customer.setResetPasswordToken(null);
        repository.save(customer);
        System.out.println("Token reset to null for user with email: " + customer.getEmail());
    }
    public void updatePassword(User customer, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(encodedPassword);

        customer.setResetPasswordToken(null);
        repository.save(customer);
        System.out.println("da xog");
    }
    public boolean changePassCurrent(String token, String curentPass, String newPass) {

        String email = token;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Optional<User> existingUser = repository.findByEmail(email);


        if (existingUser.isPresent() && passwordEncoder.matches(curentPass, existingUser.get().getPassword())) {
            existingUser.get().setPassword(passwordEncoder.encode(newPass));
            repository.save(existingUser.get());
            return true;
        } else {
            return false;
        }


    }

}