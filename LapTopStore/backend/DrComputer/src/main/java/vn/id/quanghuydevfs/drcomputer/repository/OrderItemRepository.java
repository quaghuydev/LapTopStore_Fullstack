package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItemPK;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
    @Query("SELECT oi.product, SUM(oi.quantity) AS totalQuantity " +
            "FROM OrderItem oi " +
            "GROUP BY oi.product " +
            "ORDER BY totalQuantity desc ")
    List<Object[]> findBestSellingProducts(PageRequest pageRequest);


    @Query(value = "select o  from OrderItem o where  o.product.id= :product_id")
    List<OrderItem> getOrderItemByProductId(@Param("product_id") Long product_id);
    void deleteByProduct(Product product);

}
