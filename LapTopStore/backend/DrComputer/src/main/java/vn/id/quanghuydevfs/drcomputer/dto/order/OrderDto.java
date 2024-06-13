package vn.id.quanghuydevfs.drcomputer.dto.order;

import lombok.Data;
import vn.id.quanghuydevfs.drcomputer.dto.product.ProductItemDto;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserDto;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;
import vn.id.quanghuydevfs.drcomputer.model.order.PaymentMethod;

import java.util.List;
@Data
public class OrderDto {
    private long id;
    private String fullname;
    private String street;
    private String province;
    private String district;
    private String ward;
    private String numberHouse;
    private String note;
    private List<ProductItemDto> products;
    private UserDto user;
    private boolean isPaied;
    private PaymentMethod payMethod;
    private long rewardPoints;
}
