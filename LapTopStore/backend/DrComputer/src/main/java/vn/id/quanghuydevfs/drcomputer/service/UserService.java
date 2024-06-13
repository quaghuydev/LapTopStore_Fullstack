package vn.id.quanghuydevfs.drcomputer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.id.quanghuydevfs.drcomputer.controller.auth.AuthenticationResponse;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserDto;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserQuantity;
import vn.id.quanghuydevfs.drcomputer.model.blog.Blog;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.model.token.Token;
import vn.id.quanghuydevfs.drcomputer.model.token.TokenRepository;
import vn.id.quanghuydevfs.drcomputer.model.token.TokenType;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.UserRepository;
import vn.id.quanghuydevfs.drcomputer.security.jwt.JwtService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public List<User> getUsers() {
        return repository.findAll();
    }

    //    public Optional<User> getUserById(Long id) {
//        return repository.findById(id);
//    }
    public User getUserByEmail(String email) {
        Optional<User> user = repository.findByEmail(email);
        return user.orElse(null);
    }

    @Transactional
    public boolean deleteUserById(Long id) {
        var user = repository.findById(id);

        if (user.isPresent()) {
            tokenRepository.deleteTokenByUser_Id(id);
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public Long countUsersByMonthAndYear() {
        return repository.countUsersByMonthAndYear();
    }

    public List<UserQuantity> getTopUserSell() {
        PageRequest pageRequest = PageRequest.of(0, 10); // lấy 10 người dùng đầu tiên
        List<Object[]> results = repository.findTopUsersByOrderCount(pageRequest);
        return results.stream()
                .map(result -> new UserQuantity((User) result[0], ((Long) result[1]).intValue()))
                .collect(Collectors.toList());
    }

    public AuthenticationResponse updateUserById(Long id, UserDto u) {
        User user = repository.findById(id).orElse(null);
        if (user != null) {
            user.setId(id);
            user.setFullname(u.getFullname());
            user.setEmail(u.getEmail());
            user.setPhoneNumber(u.getPhoneNumber());
            user.setRoles(u.getRoles());
            user.setEnabled(u.isEnabled()); // Cập nhật thuộc tính isEnabled
            user.setCreatedAt(u.getCreatedAt());
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            repository.save(user);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        return null;
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


    //delete


    @Transactional
    public void deleteMultiple(List<Long> ids) {
        for (Long id : ids) {
            tokenRepository.deleteTokenByUser_Id(id);
            repository.deleteById(id);
            repository.findById(id).ifPresent(product -> repository.deleteById(id));
        }
    }

}
