package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.model.order.StatusOrder;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.model.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByUserEmail(String email);
  List<Order>findByStatusAndUser(StatusOrder status,User user);

  @Query("SELECT oi.product, SUM(oi.quantity) AS totalQuantity " +
          "FROM OrderItem oi " +
          "JOIN oi.order o " +
          "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " +
          "GROUP BY oi.product " +
          "ORDER BY totalQuantity DESC")
  List<Object[]> findBestSellingProductsByMonthAndYear(int month, int year);
  @Query("SELECT c.value, SUM(oi.quantity) AS quantity " +
          "FROM OrderItem oi " +
          "JOIN oi.product p " +
          "JOIN p.category c " +
          "JOIN oi.order o " + // Thêm dấu cách trước WHERE
          "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " +
          "GROUP BY c.value " + // Sử dụng c.value thay vì c.name
          "ORDER BY quantity")
  List<Object[]> findSoldProductsByCategory(int month,int year);
  @Query("SELECT COUNT(o) " +
          "FROM Order o " +
          "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
  Integer countOrdersByMonthAndYear(int month, int year);
  @Query("SELECT DISTINCT MONTH(o.createdAt) FROM Order o")
  List<Integer> getMonthInOrder();
  @Query("SELECT DISTINCT MONTH(o.createdAt) FROM Order o where year(o.createdAt)=:year")
  List<Integer> getMonthInYearOrder(int year);
  @Query("SELECT DISTINCT year(o.createdAt) FROM Order o")
  List<Integer> getYearInOrder();
  @Query("SELECT COUNT(o.id)as total_amount FROM Order o where year(o.createdAt)=:year")

  Integer countInYear(int year);
  @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE YEAR(o.createdAt) = :year")
  BigDecimal findTotalRevenueByYear(int year);
  List<Order>findAllByUser(User user);
  @Query("SELECT u FROM Order u WHERE u.id = ?1")
  Order findByIdT(Long id);

}
