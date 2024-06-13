package vn.id.quanghuydevfs.drcomputer.dto.evaluate;


import lombok.Data;
import vn.id.quanghuydevfs.drcomputer.model.evaluate.Quality;

@Data
public class ReviewRequest {
    private Long productId;
    private int rate;
    private String content;
    private Quality quality;
    private String email;




}
