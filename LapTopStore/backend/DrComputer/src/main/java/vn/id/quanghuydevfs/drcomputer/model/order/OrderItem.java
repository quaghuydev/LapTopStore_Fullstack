package vn.id.quanghuydevfs.drcomputer.model.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;

import java.math.BigDecimal;


@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderItem {
    @EmbeddedId
    private OrderItemPK id;

    @ManyToOne
    @MapsId("orderId")
    @JsonBackReference
//    @JsonIgnore
    @ToString.Exclude
    private Order order;

    @ManyToOne
    @MapsId("productId")
    private Product product;

    private long price;
    private int quantity;
}
