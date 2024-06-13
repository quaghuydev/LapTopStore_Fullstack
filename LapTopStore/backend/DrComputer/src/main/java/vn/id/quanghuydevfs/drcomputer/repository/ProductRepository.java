package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.product.Category;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    Page<Product> getProductsByTitleContainsIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategory_ValueContainingIgnoreCaseAndTitleContainingIgnoreCase(String category, String keyword, Pageable pageable);

    Page<Product> findByCategory_Value(String category, Pageable pageable);
    @Query("SELECT MAX(p.id) FROM Product p")
    Long findMaxId();

    List<Product> findByCategory(Category category);

}
