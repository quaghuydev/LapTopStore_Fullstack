package vn.id.quanghuydevfs.drcomputer.controller.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.dto.evaluate.ReviewRequest;
import vn.id.quanghuydevfs.drcomputer.model.review.DeleteReviewRequest;
import vn.id.quanghuydevfs.drcomputer.model.review.Review;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.UserRepository;
import vn.id.quanghuydevfs.drcomputer.service.ReviewService;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
@CrossOrigin
public class ReviewController {
    @Autowired
    public ReviewService reviewService;
    @Autowired
    public UserRepository repository;

//    @PostMapping("/create")
//    public ResponseEntity<Review> createReviewHandler(@RequestBody ReviewRequest req) throws Exception {
//        User user = repository.findBEmail(req.getEmail());
//        System.out.println("product id " + req.getProductId() + " - " + req.getContent());
//        Review rv = reviewService.createReview(req, user);
//
//        if (rv.getId() == 0 || rv.getId() == -999) {
//            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
//        }
//
//        return new ResponseEntity<Review>(rv, HttpStatus.ACCEPTED);
//    }
@PostMapping("/create")
public ResponseEntity<?> createReviewHandler(@RequestBody ReviewRequest req) {
    User user = repository.findBEmail(req.getEmail());

    try {
        Review rv = reviewService.createReview(req, user);

        // Kiểm tra xem người dùng đã mua hàng chưa
        if (rv.getId() == -999) {
            return new ResponseEntity<>(rv, HttpStatus.FORBIDDEN); // Hoặc HttpStatus.BAD_REQUEST
        }

        return new ResponseEntity<>(rv, HttpStatus.CREATED);
    } catch (Exception e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

    @GetMapping("")
    public List<Review> getAllReview() {
        List<Review> listReview = reviewService.getAll();
        if (listReview.isEmpty()) {
            return null;
        }
        return listReview;
    }

    @GetMapping(value = "/all")
    public List<Review> getAll(@RequestParam Long product_id) throws Exception {
        if (product_id == null || product_id < 0) {
            throw new Exception("No product id");
        }
        try {
            List<Review> rv = reviewService.getAllReviewByProduct(product_id);
            if (rv.isEmpty()) {
                throw new Exception("No product ");
            }
            return rv;
        } catch (Exception ee) {
            System.out.println(ee.getMessage());
        }
        return new ArrayList<>();

    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestParam Long id){
        System.out.println(id);
       String res= reviewService.deleteReview(id);
        return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/deletes")
    public ResponseEntity<String> deleteItems(@RequestBody DeleteReviewRequest request) {
        try {
            reviewService.deleteMultiple(request.getReviewIds());
            return new ResponseEntity<>("Reviews deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting reviews: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
