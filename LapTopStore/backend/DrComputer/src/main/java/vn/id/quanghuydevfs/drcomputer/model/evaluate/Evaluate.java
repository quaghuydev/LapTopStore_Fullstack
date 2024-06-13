package vn.id.quanghuydevfs.drcomputer.model.evaluate;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.model.user.Roles;
import vn.id.quanghuydevfs.drcomputer.model.user.User;

import java.sql.Date;
import java.time.LocalDate;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int rate;
    @Enumerated(EnumType.STRING)
    private Quality quality;
    private String content;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;
    private Roles roles;
    private String reply;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
