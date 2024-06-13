package vn.id.quanghuydevfs.drcomputer.controller.admin.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.dto.category.CategoryQuantity;
import vn.id.quanghuydevfs.drcomputer.dto.product.ProductQuantity;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserQuantity;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.service.OrderService;
import vn.id.quanghuydevfs.drcomputer.service.ProductService;
import vn.id.quanghuydevfs.drcomputer.service.UserService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/management/dashboard")
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN')")
@RequiredArgsConstructor
public class DashboardController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/best-selling-products")
    public ResponseEntity<List<ProductQuantity>> getBestSelling() {
        return ResponseEntity.ok(productService.getBestSellingProducts());
    }

    @PostMapping("/best-selling")
    public ResponseEntity<List<Product>> getBestSellingProductsByMonthAndYear(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(orderService.getBestSellingProductsByMonthAndYear(month, year));
    }

    @GetMapping("/sold-by-category")
    public ResponseEntity<List<CategoryQuantity>> getSoldProductsByCategory(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(productService.getSoldProductsByCategory(month,year));
    }
    @GetMapping("/count-order-in-year")
    public ResponseEntity<Integer> countOrder(@RequestParam int year) {
        return ResponseEntity.ok(orderService.countOrderInYear(year));
    }

    @GetMapping("/quatity-order-by-month-year")
    public ResponseEntity<Integer> countOrdersByMonthAndYear(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(orderService.countOrdersByMonthAndYear(month, year));
    }

    @GetMapping("/count-user")
    public ResponseEntity<Long> countUsersByMonthAndYear() {
        return ResponseEntity.ok(userService.countUsersByMonthAndYear());
    }

    @GetMapping("/top-user-selling")
    public ResponseEntity<List<UserQuantity>> countUsers() {
        return ResponseEntity.ok(userService.getTopUserSell());
    }
    @GetMapping("/total-revenue")
    public ResponseEntity<BigDecimal> getTotalRevenueByYear(@RequestParam int year) {
        return ResponseEntity.ok(orderService.getTotalRevenueByYear(year));
    }
    @GetMapping("/get-months")
    public ResponseEntity<List<Integer>> getMonth() {
        return ResponseEntity.ok(orderService.getMonth());
    }
    @GetMapping("/get-months/{year}")
    public ResponseEntity<List<Integer>> getMonthInYear(@PathVariable int year) {
        return ResponseEntity.ok(orderService.getMonthInYear(year));
    }
    @GetMapping("/get-years")
    public ResponseEntity<List<Integer>> getYear() {
        return ResponseEntity.ok(orderService.getYear());
    }
}
