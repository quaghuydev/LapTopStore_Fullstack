package vn.id.quanghuydevfs.drcomputer.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.id.quanghuydevfs.drcomputer.dto.category.CategoryQuantity;
import vn.id.quanghuydevfs.drcomputer.dto.product.ProductDto;
import vn.id.quanghuydevfs.drcomputer.dto.product.ProductQuantity;
import vn.id.quanghuydevfs.drcomputer.model.product.Category;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.repository.OrderItemRepository;
import vn.id.quanghuydevfs.drcomputer.repository.OrderRepository;
import vn.id.quanghuydevfs.drcomputer.repository.ProductRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Page<Product> getProducts(int page, int size, String sort, String category, String search) {
        Sort sorting;
        if (sort.equals("id")) {
            sorting = Sort.by(Sort.Order.asc("id"));
        } else {
            sorting = Sort.by(sort.equals("asc") ? Sort.Order.asc("price") : Sort.Order.desc("price"));
        }

        Pageable pageable;
        if ("all".equalsIgnoreCase(category)) {
            pageable = PageRequest.of(page - 1, size, sorting);
        } else {
            pageable = PageRequest.of(page - 1, size, sorting);
            if (search != null && !search.isEmpty()) {
                return productRepository.findByCategory_ValueContainingIgnoreCaseAndTitleContainingIgnoreCase(category, search, pageable);
            } else {
                return productRepository.findByCategory_Value(category, pageable);
            }
        }

        if (search != null && !search.isEmpty()) {
            return productRepository.getProductsByTitleContainsIgnoreCase(search, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> getProductByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.getProductsByTitleContainsIgnoreCase(name, pageable);
    }

    public List<Product> getProductsByCategory(String categoryValue) {
        Category category = categoryService.getCategoryByValue(categoryValue);
        return productRepository.findByCategory(category);
    }

    public Product add(ProductDto p) {
        // Lấy giá trị id lớn nhất từ cơ sở dữ liệu
        long maxId = productRepository.findMaxId();

        // Tạo đối tượng Product với id mới
        var category = categoryService.getCategoryByValue(p.getCategory());
        var product = Product.builder()
                .id(maxId + 1) // Sử dụng id mới
                .title(p.getTitle())
                .description(p.getDescription())
                .category(category)
                .price(p.getPrice())
                .storage(p.getStorage())
                .img1(p.getImg1())
                .img2(p.getImg2())
                .sale(p.getSale())
                .build();

        // Lưu sản phẩm vào cơ sở dữ liệu
        productRepository.saveAndFlush(product);

        return product;
    }

    @Transactional
    public void importProductFromExcel(MultipartFile file) throws IOException {
        InputStream io = file.getInputStream();
        Workbook wb = WorkbookFactory.create(io);
        Sheet sheet = wb.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Bỏ qua hàng đầu tiên (header)

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            long id = (long) row.getCell(0).getNumericCellValue(); // Lấy giá trị id từ ô numeric
            String title = row.getCell(1).getStringCellValue();
            String description = row.getCell(2).getStringCellValue();
            String categoryName = row.getCell(3).getStringCellValue();
            long price = (long) row.getCell(4).getNumericCellValue();
            int storage = (int) row.getCell(5).getNumericCellValue();
            int sale = (int) row.getCell(6).getNumericCellValue();
            String img1 = row.getCell(7).getStringCellValue();
            String img2 = row.getCell(8).getStringCellValue();

            // Tìm hoặc tạo mới Category dựa trên tên
            Category category = categoryService.getCategoryByName(categoryName);

            // Tạo đối tượng Product
            Product product = Product.builder()
                    .title(title)
                    .description(description)
                    .category(category)
                    .price(price)
                    .storage(storage)
                    .sale(sale)
                    .img1(img1)
                    .img2(img2)
                    .build();

            // Kiểm tra nếu ID tồn tại trong file Excel
            if (id != 0) {
                if (productRepository.existsById(id)) {
                    // Nếu sản phẩm đã tồn tại, cập nhật sản phẩm đó
                    product.setId(id);
                    productRepository.save(product);
                } else {
                    // Nếu sản phẩm không tồn tại trong cơ sở dữ liệu, lưu sản phẩm với ID từ file Excel
                    product.setId(id);
                    productRepository.save(product);
                }
            } else {
                // Nếu ID không được đặt trong file Excel, để JPA tự sinh ID mới
                productRepository.save(product);
            }
        }
        wb.close();
        io.close();
    }

    public Product update(Long id, ProductDto p) {
        var category = categoryService.getCategoryByValue(p.getCategory());
        Product product = productRepository.findById(id).orElseThrow();
        product.setId(id);
        product.setTitle(p.getTitle());
        product.setDescription(p.getDescription());
        product.setCategory(category);
        product.setPrice(p.getPrice());
        product.setStorage(p.getStorage());
        product.setImg1(p.getImg1());
        product.setImg2(p.getImg2());
        product.setSale(p.getSale());
        productRepository.saveAndFlush(product);
        return product;
    }

    public boolean delete(Long id) {
        Optional<Product> p = productRepository.findById(id);
        if (p.isPresent()) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void deleteMultiple(List<Long> ids) {
        for (Long id : ids) {
            productRepository.findById(id).ifPresent(product -> productRepository.deleteById(id));
        }
    }

    public List<ProductQuantity> getBestSellingProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Object[]> results = orderItemRepository.findBestSellingProducts(pageRequest);
        return results.stream()
                .map(result -> new ProductQuantity((Product) result[0], (long) result[1]))
                .collect(Collectors.toList());
    }

    public List<CategoryQuantity> getSoldProductsByCategory(int month,int year) {
        List<Object[]> results = orderRepository.findSoldProductsByCategory(month,year);
        return results.stream().map(result -> new CategoryQuantity((String) result[0], ((Long) result[1]).intValue()))
                .collect(Collectors.toList());
    }


}
