package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT u FROM OrderItem u WHERE u.order = ?1")
    public OrderItem findByOrderId(Long id);

}
