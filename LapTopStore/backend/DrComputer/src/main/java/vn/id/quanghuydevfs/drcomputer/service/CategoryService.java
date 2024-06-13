package vn.id.quanghuydevfs.drcomputer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.quanghuydevfs.drcomputer.dto.category.CategoryReqDto;
import vn.id.quanghuydevfs.drcomputer.exception.ResourceNotFoundException;
import vn.id.quanghuydevfs.drcomputer.model.product.Category;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

    public List<Category> getCategorys() {
        return repository.findAll();
    }

    public Category getCategoryByValue(String value) {
        return repository.findByValue(value);
    }
    public Category getCategoryByName(String name) {
        return repository.findByNameContainsIgnoreCase(name);
    }

    public Category addCategory(CategoryReqDto req) {
        var category = Category.builder().name(req.getName()).value(req.getValue()).build();
        return repository.save(category);
    }

    public Category updateCategory(int id, CategoryReqDto req) throws ResourceNotFoundException {
        var category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("not found category"));
        category.setName(req.getName());
        category.setValue(req.getValue());
        return repository.save(category);
    }

    public boolean deleteCategory(int id) {
        Optional<Category> p = repository.findById(id);
        if (p.isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    public void deleteCategories(List<Integer> ids) {
        repository.deleteAllById(ids);
    }
}
