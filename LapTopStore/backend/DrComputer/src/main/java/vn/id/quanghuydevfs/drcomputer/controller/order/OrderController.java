package vn.id.quanghuydevfs.drcomputer.controller.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.dto.order.OrderDto;
import vn.id.quanghuydevfs.drcomputer.dto.order.StatusOrderDTO;
import vn.id.quanghuydevfs.drcomputer.exception.ResourceNotFoundException;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.model.order.StatusOrder;
import vn.id.quanghuydevfs.drcomputer.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService service;


    @GetMapping("")
    public ResponseEntity<List<Order>> getOrders() {
        var orders = service.getOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{status}/orders")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status,@RequestParam String email) {
            return ResponseEntity.ok(service.getOrdersByStatus(status,email));
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

    @CrossOrigin(origins = "*")
    @GetMapping("/my_order/{email}")
    public ResponseEntity<?> myOrder(@PathVariable String email) {
        List<Order> orders = service.getOrdersByUserEmail(email);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/all")
    public ResponseEntity<List<StatusOrderDTO>> getStatusOrders() {
        List<StatusOrderDTO> statusOrderDTOList = new ArrayList<>();
        for (StatusOrder statusOrder : StatusOrder.values()) {
            String description = getDescriptionForStatus(statusOrder);
            statusOrderDTOList.add(StatusOrderDTO.builder().status(statusOrder.name()).description(description).build());
        }
        return ResponseEntity.ok(statusOrderDTOList);
    }

    @PutMapping("/cancel-order/{id}")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable long id) {
        return ResponseEntity.ok(service.cancelOrder(id));
    }
    @PutMapping("/return-order/{id}")
    public ResponseEntity<Boolean> returnOrder(@PathVariable long id) {
        return ResponseEntity.ok(service.returnOrder(id));
    }


    private String getDescriptionForStatus(StatusOrder statusOrder) {
        return switch (statusOrder) {
            case pending -> "Chờ xác nhận";
            case picking -> "Đang lấy hàng";
            case transporting -> "Đang vận chuyển";
            case delivering -> "Đang giao hàng";
            case delivered -> "Đã giao hàng";
            case cancel -> "Đã hủy";
            case returned -> "Đã trả hàng";
            default -> "Không xác định";
        };


    }
}
