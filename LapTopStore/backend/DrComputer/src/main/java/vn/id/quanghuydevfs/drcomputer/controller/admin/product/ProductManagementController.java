package vn.id.quanghuydevfs.drcomputer.controller.admin.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.id.quanghuydevfs.drcomputer.dto.product.DeleteProductRequest;
import vn.id.quanghuydevfs.drcomputer.dto.product.ProductDto;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.service.ProductService;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@RequestMapping("/api/v1/management/product")
@RequiredArgsConstructor
public class ProductManagementController {
    private final ProductService productService;
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.add(productDto));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.update(id, productDto));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.delete(id));
    }
    @DeleteMapping("/delete/products")
    public ResponseEntity<Void> deleteProduct(@RequestBody DeleteProductRequest request) {
        productService.deleteMultiple(request.getProductIds());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<?> importProductFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            productService.importProductFromExcel(file);
            return ResponseEntity.ok("Sản phẩm được import thành công từ file Excel.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi import sản phẩm từ file Excel: " + e.getMessage());
        }
    }
}