package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.product.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByValue(String value);
    Category findByNameContainsIgnoreCase(String name);
}
