package vn.id.quanghuydevfs.drcomputer.dto.user;

import lombok.Data;

@Data
public class ChangePass {
    private String email;
    private  String newPasss;
    private String currentPass;
}
