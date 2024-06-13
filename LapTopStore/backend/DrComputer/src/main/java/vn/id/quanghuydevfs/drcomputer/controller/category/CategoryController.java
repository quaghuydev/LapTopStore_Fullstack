package vn.id.quanghuydevfs.drcomputer.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.dto.category.CategoryReqDto;
import vn.id.quanghuydevfs.drcomputer.exception.ResourceNotFoundException;
import vn.id.quanghuydevfs.drcomputer.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("api/v1/category")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping("/all")
    public ResponseEntity<?> getCategorys() {
        return ResponseEntity.ok(service.getCategorys());
    }

    @PostMapping("/{value}")
    public ResponseEntity<?> getCategoryByValue(@PathVariable String value) {
        return ResponseEntity.ok(service.getCategoryByValue(value));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CategoryReqDto req) {
        return ResponseEntity.ok(service.addCategory(req));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CategoryReqDto req) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.updateCategory(id, req));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.deleteCategory(id));
    }
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<?> deleteMultiple(@RequestBody List<Integer> ids) {
        service.deleteCategories(ids);
        return ResponseEntity.ok("Categories deleted successfully.");
    }
}
