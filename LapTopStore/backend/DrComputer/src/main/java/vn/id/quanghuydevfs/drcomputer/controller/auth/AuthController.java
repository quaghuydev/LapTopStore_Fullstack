package vn.id.quanghuydevfs.drcomputer.controller.auth;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.dto.auth.AuthenticationDto;
import vn.id.quanghuydevfs.drcomputer.dto.auth.RegisterDto;
import vn.id.quanghuydevfs.drcomputer.dto.user.ChangePass;
import vn.id.quanghuydevfs.drcomputer.dto.user.ForgotRequest;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserDto;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.security.jwt.JwtService;
import vn.id.quanghuydevfs.drcomputer.service.AuthService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jakarta.mail.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDto request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationDto request) {

        return ResponseEntity.ok(authService.authenticate(request));
    }
    @PostMapping("/login/google")
    public ResponseEntity<AuthenticationResponse> authGoogle(@RequestBody UserDto user) {

        return ResponseEntity.ok(authService.authGoogle(user));
    }
    @GetMapping("/current-user")
    public ResponseEntity<UserDto> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            User user = (User) principal;
            UserDto userDto = new UserDto();
            userDto.setEmail(user.getEmail());
            userDto.setFullname(user.getFullname());
            userDto.setPhoneNumber(user.getPhoneNumber());
            userDto.setRoles(user.getRoles());
            return ResponseEntity.ok(userDto);
        } else {
            throw new IllegalStateException("Principal is not an instance of UserDetails: " + principal.getClass());
        }
    }
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authService.refreshToken(request, response);
    }
    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        authService.logout(request, response,authentication);
    }
    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestBody ForgotRequest forgot, Model model) {
        String email = forgot.getEmail();
        String token = RandomStringUtils.randomNumeric(12);

        try {
            authService.updateResetPasswordToken(token, email);

            sendEmail(email, token);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (IOException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (  MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "true";
    }
    @GetMapping("/reset_password")
    public boolean showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = authService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            System.out.println("thay doi sai");
            model.addAttribute("message", "Invalid Token");
            return  false;
        }

        return true;
    }
    @PostMapping("/reset_password")
    public boolean processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = authService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return false;
        } else {
            authService.updatePassword(user, password);

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return true;
    }
//    @PostMapping("/reset_password")
//    public boolean retsetChange(HttpServletRequest request, Model model) {
//
//        return true;
//    }

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("contact@drcomputer.com", "DrComputer Support");
        helper.setTo(recipientEmail);

        String subject = "Đây là mã xác nhận của bạn";

        String content = "<p>Xin chào,</p>"
                + "<p>Bạn đã gửi cho chúng tôi yêu cầu làm mới mật khẩu của bạn.</p>"
                + "<p>Dưới đây là mã xác nhận của bạn</p>"
                + "<p> Mã xác nhận: "+link+"</p>"
                +"<p> Mã xác nhận sẽ hết hiệu lực sau 10 phút"
                + "<br>"
                + "<p>Hãy bỏ qua email này nếu bạn nhớ mật khẩu của mình, "
                + "hoặc bạn không yêu cầu việc này.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }
    @PostMapping("/change-password-profile")
    public boolean changePassProfile( @RequestBody ChangePass changePass) {
        if (changePass.getEmail()==null || changePass.getCurrentPass()==null || changePass.getNewPasss()==null){
            return false;
        }
      return authService.changePassCurrent(changePass.getEmail(),changePass.getCurrentPass(),changePass.getNewPasss());

    }
    @GetMapping("/checkPermission")
    public ResponseEntity<Boolean> checkPermission(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtService.isAdmin(token)) {
                return ResponseEntity.ok().body(true);
            }
            return ResponseEntity.status(403).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(false);
        }
    }

}

