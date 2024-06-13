package vn.id.quanghuydevfs.drcomputer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.quanghuydevfs.drcomputer.model.blog.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
}
