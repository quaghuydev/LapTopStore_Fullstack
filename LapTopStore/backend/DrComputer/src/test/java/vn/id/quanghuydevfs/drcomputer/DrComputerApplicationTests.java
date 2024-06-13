package vn.id.quanghuydevfs.drcomputer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.id.quanghuydevfs.drcomputer.dto.order.OrderDto;
import vn.id.quanghuydevfs.drcomputer.service.OrderService;

@SpringBootTest
class DrComputerApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    public void testCreateOrder() {

        Assertions.assertNotNull(orderService.getOrders());
        // Kiểm tra xem thông tin trong orderResponse có chính xác hay không
        // Bạn có thể kiểm tra các thuộc tính của orderResponse.getOrder() tại đây
    }

}
