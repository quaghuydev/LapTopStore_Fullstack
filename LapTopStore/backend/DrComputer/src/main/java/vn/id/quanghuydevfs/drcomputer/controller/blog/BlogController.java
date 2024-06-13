package vn.id.quanghuydevfs.drcomputer.controller.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.id.quanghuydevfs.drcomputer.dto.blog.BlogDto;
import vn.id.quanghuydevfs.drcomputer.dto.blog.DeleteBlogRequest;
import vn.id.quanghuydevfs.drcomputer.model.blog.Blog;
import vn.id.quanghuydevfs.drcomputer.service.BlogService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/blogs")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping()
    public List<Blog> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    @GetMapping("/{id}")
    public Optional<Blog> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Blog> addBlog(@RequestBody BlogDto blogDto) {
        return ResponseEntity.ok(blogService.add(blogDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long id, @RequestBody BlogDto blogDto) {
        if (id != null) {
            return ResponseEntity.ok(blogService.update(id, blogDto));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletes")
    public ResponseEntity<Void> deleteProduct(@RequestBody DeleteBlogRequest request) {
        blogService.deleteMultiple(request.getBlogIds());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<?> importProductFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            blogService.importBlogFromExcel(file);
            return ResponseEntity.ok("Bài viết được import thành công từ file Excel.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi import bài viết từ file Excel: " + e.getMessage());
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Có lỗi xảy ra khi phân tích ngày: " + e.getMessage());
        }
    }
}
