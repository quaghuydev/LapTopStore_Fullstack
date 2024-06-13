package vn.id.quanghuydevfs.drcomputer.dto.evaluate;

import lombok.Data;
import vn.id.quanghuydevfs.drcomputer.model.evaluate.Quality;
import vn.id.quanghuydevfs.drcomputer.model.user.Roles;

@Data
public class EvaluateRequest {
    private Roles role;
    private int rate;
    private Quality quality;
    private String content;
    private String email;
    private Long order_id;
    private String reply;
}
