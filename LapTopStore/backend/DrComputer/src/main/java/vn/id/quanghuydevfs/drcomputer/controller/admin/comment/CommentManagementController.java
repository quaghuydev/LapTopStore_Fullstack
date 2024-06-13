package vn.id.quanghuydevfs.drcomputer.controller.admin.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.id.quanghuydevfs.drcomputer.model.comment.Comment;
import vn.id.quanghuydevfs.drcomputer.model.comment.CommentResponse;
import vn.id.quanghuydevfs.drcomputer.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/management/comment")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@CrossOrigin(origins = "http://localhost:3000")

public class CommentManagementController {
    @Autowired
    private CommentRepository commentRepository;
    @GetMapping("/all")

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
}
