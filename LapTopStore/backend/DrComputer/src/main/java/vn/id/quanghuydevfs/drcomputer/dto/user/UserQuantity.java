package vn.id.quanghuydevfs.drcomputer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQuantity {
    private User user;
    private Integer quantity;
}
