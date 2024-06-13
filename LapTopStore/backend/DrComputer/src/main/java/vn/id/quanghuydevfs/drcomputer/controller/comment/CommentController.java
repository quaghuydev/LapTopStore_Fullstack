package vn.id.quanghuydevfs.drcomputer.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.model.comment.Comment;
import vn.id.quanghuydevfs.drcomputer.model.comment.CommentRequest;
import vn.id.quanghuydevfs.drcomputer.model.comment.CommentResponse;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.CommentRepository;
import vn.id.quanghuydevfs.drcomputer.repository.UserRepository;
import vn.id.quanghuydevfs.drcomputer.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;


    // Endpoint to add a comment to a product
    @PostMapping("/{productId}/comments")
    public ResponseEntity<Comment> addCommentToProduct(@PathVariable Long productId, @RequestBody CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null) {
//            // Lấy tên người dùng
//            String email = authentication.getName();
//            try {
//                User user = userRepository.findByEmail(email).orElseThrow();
//                comment.setUser(user);
//            }catch (Exception e){
//                return ResponseEntity.badRequest().build();
//            }
//        }

        User user = userRepository.findByEmail(commentRequest.getEmail()).orElseThrow();
        comment.setUser(user);
        Product product = productService.getProductById(productId)
                .orElseThrow();
        comment.setProduct(product);
        comment.setImage(commentRequest.getImage());
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }

    @PostMapping("/{commentID}/comments-child")
    public ResponseEntity<Comment> addCommentChild(@PathVariable Long commentID, @RequestBody CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        User user = userRepository.findByEmail(commentRequest.getEmail()).orElseThrow();
        comment.setUser(user);
        Comment comment1 = commentRepository.findById(commentID)
                .orElseThrow();
        comment.setComment(comment1);
        comment.setImage(commentRequest.getImage());
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }

    // Endpoint to get all comments of a product
    @GetMapping("/{productId}/comments")
    public ResponseEntity<List<CommentResponse>> getAllCommentsOfProduct(@PathVariable Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        List<CommentResponse> commentResponseArrayList = new ArrayList<>();
        for (Comment c : comments) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setContent(c.getContent());
            commentResponse.setId(c.getId());
            commentResponse.setTime(c.getTime());
            commentResponse.setImage(c.getImage());
            commentResponse.setUser(c.getUser());
            commentResponse.setCommentChild(commentRepository.findByComment_Id(c.getId()));
            commentResponseArrayList.add(commentResponse);
        }
        return ResponseEntity.ok(commentResponseArrayList);
    }

    @GetMapping("/admin/comments")
    public ResponseEntity<List<CommentResponse>> getAllCommentsAdmin() {
        List<Comment> comments = commentRepository.findAllComment();
        List<CommentResponse> commentResponseArrayList = new ArrayList<>();
        for (Comment c : comments) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setContent(c.getContent());
            commentResponse.setId(c.getId());
            commentResponse.setTime(c.getTime());
            commentResponse.setImage(c.getImage());
            commentResponse.setUser(c.getUser());
            commentResponse.setProduct(c.getProduct());
            commentResponse.setCommentChild(commentRepository.findByComment_Id(c.getId()));
            commentResponseArrayList.add(commentResponse);
        }
        return ResponseEntity.ok(commentResponseArrayList);
    }

    @GetMapping("/delete/comments/{idComment}")
    public ResponseEntity<?> deleteComment(@PathVariable Long idComment) {
        List<Comment> comments = commentRepository.findByComment_Id(idComment);
        if (comments.size() > 0){
            for (Comment c:comments) {
                commentRepository.deleteById(c.getId());
            }
        }
        commentRepository.deleteById(idComment);
        return ResponseEntity.ok(null);
    }
}




