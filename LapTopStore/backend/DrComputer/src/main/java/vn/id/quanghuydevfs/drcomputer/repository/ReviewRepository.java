package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.id.quanghuydevfs.drcomputer.model.review.Review;
import vn.id.quanghuydevfs.drcomputer.model.user.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select r from Review r  where r.product.id = :product_id and r.user.id= :user_id")
    Review checkExist(@Param("product_id") Long product_id, @Param("user_id") Long user_id);
    @Query(value = "select r from Review r  where r.product.id = :product_id ")
    List<Review> getALlByIdProduct(@Param("product_id") Long product_id);


    @Query(value = "delete  from Review   where id = :product_id ")
    void deleteByItem(@Param("product_id") Long product_id);

}
