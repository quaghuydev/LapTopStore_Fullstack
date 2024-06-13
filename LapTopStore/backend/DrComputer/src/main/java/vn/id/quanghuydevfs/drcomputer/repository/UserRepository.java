package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.id.quanghuydevfs.drcomputer.model.user.User;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User>findByEmail(String email);
    @Query("SELECT COUNT(u) " +
            "FROM User u ")
    Long countUsersByMonthAndYear();
    @Query("SELECT u, COUNT(o.id) AS totalAmount " +
            "FROM User u " +
            "JOIN Order o ON u.id = o.user.id " +
            "GROUP BY u.id " +
            "ORDER BY totalAmount DESC")
    List<Object[]> findTopUsersByOrderCount(PageRequest pageRequest);
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findBEmail(String email);
    public User findByResetPasswordToken(String token);
}