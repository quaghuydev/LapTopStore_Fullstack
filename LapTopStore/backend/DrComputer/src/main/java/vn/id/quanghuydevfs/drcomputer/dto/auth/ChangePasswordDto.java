package vn.id.quanghuydevfs.drcomputer.dto.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordDto {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;
}
