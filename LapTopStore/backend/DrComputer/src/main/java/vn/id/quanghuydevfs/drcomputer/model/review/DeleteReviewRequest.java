package vn.id.quanghuydevfs.drcomputer.model.review;

import lombok.Data;

import java.util.List;
@Data
public class DeleteReviewRequest {
    private List<Long> reviewIds;
}
