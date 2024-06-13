package vn.id.quanghuydevfs.drcomputer.controller.admin.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.controller.order.OrderResponse;
import vn.id.quanghuydevfs.drcomputer.dto.order.OrderDto;
import vn.id.quanghuydevfs.drcomputer.exception.ResourceNotFoundException;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-management/order")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderManagementController {
    private final OrderService service;

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getOrders() {
        var orders = service.getOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable long id) {
        var order = service.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderDto orderDto) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.createOrder(orderDto));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteOrder(@PathVariable long id) {
        return ResponseEntity.ok(service.delete(id));
    }


    @DeleteMapping("/delete/orders")
    public ResponseEntity<Void> deleteMultipleOrders(@RequestBody List<Long> ids) {
        service.deleteMultiple(ids);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/confirm/{id}")
    public ResponseEntity<Boolean> confirmOrder(@PathVariable long id) {

        return ResponseEntity.ok(service.confirmOrder(id));
    }

    @PutMapping("/confirm/orders")
    public ResponseEntity<Void> confirmOrders(@RequestBody List<Long> ids) {
        service.confirmOrders(ids);
        return ResponseEntity.noContent().build();
    }

}
