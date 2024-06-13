    package vn.id.quanghuydevfs.drcomputer.model.review;
    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.OnDelete;
    import org.hibernate.annotations.OnDeleteAction;
    import vn.id.quanghuydevfs.drcomputer.model.evaluate.Quality;
    import vn.id.quanghuydevfs.drcomputer.model.order.Order;
    import vn.id.quanghuydevfs.drcomputer.model.product.Product;
    import vn.id.quanghuydevfs.drcomputer.model.user.Roles;
    import vn.id.quanghuydevfs.drcomputer.model.user.User;

    import java.time.LocalDate;

    @Entity
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class Review {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        private int rate;
        @Enumerated(EnumType.STRING)
        private Quality quality;
        private String content;
        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;


        @ManyToOne
        @JoinColumn(name = "product_id") 
//        @JsonBackReference
//        @ToString.Exclude
          private Product product;

        private LocalDate createdAt;
        private LocalDate updatedAt;

    }
