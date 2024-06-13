package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.evaluate.Evaluate;


import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluateRepository extends JpaRepository<Evaluate, Long> {
    @Query("SELECT u FROM Evaluate u WHERE u.order.id = ?1")
    Optional<Evaluate> findByOrder(Long id);

    @Query(value = "select u from Evaluate u  where u.order.id = :order_id ")
    List<Evaluate> getALlByIdProduct(@Param("order_id") Long order_id);


    @Query(value = "delete  from Evaluate   where id = :order_id ")
    void deleteByItem(@Param("order_id ") Long order_id );
}
