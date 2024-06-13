package vn.id.quanghuydevfs.drcomputer.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import vn.id.quanghuydevfs.drcomputer.model.comment.Comment;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;
import vn.id.quanghuydevfs.drcomputer.model.review.Review;

import java.util.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
    @Id
    private long id;
    @Column(length = 65000)
    private String title;
    @Column(length = 65000)
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private long price;
    private int storage;
    private int sale;
    private String img1;
    private String img2;
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    @JsonIgnore

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
    @JsonIgnore

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem>orderItems;
}
