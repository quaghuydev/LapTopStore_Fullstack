package vn.id.quanghuydevfs.drcomputer.controller.admin.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.quanghuydevfs.drcomputer.model.review.Review;
import vn.id.quanghuydevfs.drcomputer.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/management/review")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@RequiredArgsConstructor
@CrossOrigin("*")


public class ReviewManagementController {
    @Autowired
    public ReviewService reviewService;
    @GetMapping("")
    public List<Review> getAllReview() {
        List<Review> listReview = reviewService.getAll();
        if (listReview.isEmpty()) {
            return null;
        }
        return listReview;
    }

}
