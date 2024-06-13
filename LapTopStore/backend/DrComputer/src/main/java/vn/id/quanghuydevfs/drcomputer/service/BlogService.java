package vn.id.quanghuydevfs.drcomputer.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.id.quanghuydevfs.drcomputer.dto.blog.BlogDto;
import vn.id.quanghuydevfs.drcomputer.model.blog.Blog;
import vn.id.quanghuydevfs.drcomputer.model.product.Category;
import vn.id.quanghuydevfs.drcomputer.repository.BlogRepository;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final CategoryService categoryService;

    @Autowired
    public BlogService(BlogRepository blogRepository, CategoryService categoryService) {
        this.blogRepository = blogRepository;
        this.categoryService = categoryService;
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    public Blog add(BlogDto blogDto) {
        Date currentDate = new Date();
        var category = categoryService.getCategoryByValue(blogDto.getCategory());
        var newBlog = Blog.builder()
                .title(blogDto.getTitle())
                .category(category)
                .description(blogDto.getDescription())
                .author(blogDto.getAuthor())
                .content(blogDto.getContent())
                .img(blogDto.getImg())
                .dataCreate(currentDate)
                .build();
        blogRepository.save(newBlog);
        // Lưu bài blog mới vào cơ sở dữ liệu và trả về kết quả
        return newBlog;
    }

    //update

    public Blog update(Long id, BlogDto blogDto) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            blog.setTitle(blogDto.getTitle());
            blog.setDescription(blogDto.getDescription());
            blog.setAuthor(blogDto.getAuthor());
            blog.setContent(blogDto.getContent());
            blog.setImg(blogDto.getImg());
            blog.setDataCreate(blogDto.getDataCreate());

            Category category = categoryService.getCategoryByValue(blogDto.getCategory());
            if (category != null) {
                blog.setCategory(category);
            } else {
                // Handle category not found, throw an exception or create a new category
            }

            return blogRepository.save(blog);
        } else {
            throw new RuntimeException("Blog not found");
        }
    }

    //delete
    public boolean delete(Long id) {
        Optional<Blog> p = blogRepository.findById(id);
        if (p.isPresent()) {
            blogRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void deleteMultiple(List<Long> ids) {
        for (Long id : ids) {
            blogRepository.findById(id).ifPresent(product -> blogRepository.deleteById(id));
        }
    }

    @Transactional
    public void importBlogFromExcel(MultipartFile file) throws IOException, ParseException {
        InputStream io = file.getInputStream();
        Workbook wb = WorkbookFactory.create(io);
        Sheet sheet = wb.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Bỏ qua hàng đầu tiên (header)

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            long id = (long) row.getCell(0).getNumericCellValue(); // Lấy giá trị id từ ô numeric
            String title = row.getCell(1).getStringCellValue();
            String categoryName = row.getCell(2).getStringCellValue();
            String description = row.getCell(3).getStringCellValue();
            String author = row.getCell(4).getStringCellValue();
            String content = row.getCell(5).getStringCellValue();
            String img = row.getCell(6).getStringCellValue();
            String dateCreateString = row.getCell(7).getStringCellValue();
            Date dateCreate = dateFormat.parse(dateCreateString); // Chuyển đổi chuỗi ngày thành đối tượng Date

            // Tìm hoặc tạo mới Category dựa trên tên
            Category category = categoryService.getCategoryByName(categoryName);

            // Tạo đối tượng Blog
            Blog blog = Blog.builder()
                    .title(title)
                    .category(category)
                    .description(description)
                    .author(author)
                    .content(content)
                    .img(img)
                    .dataCreate(dateCreate)
                    .build();

            // Kiểm tra nếu ID tồn tại trong file Excel
            if (id != 0) {
                if (blogRepository.existsById(id)) {
                    // Nếu bài viết đã tồn tại, cập nhật bài viết đó
                    blog.setId(id);
                    blogRepository.save(blog);
                } else {
                    // Nếu bài viết không tồn tại trong cơ sở dữ liệu, lưu bài viết với ID từ file Excel
                    blog.setId(id);
                    blogRepository.save(blog);
                }
            } else {
                // Nếu ID không được đặt trong file Excel, để JPA tự sinh ID mới
                blogRepository.save(blog);
            }
        }
        wb.close();
        io.close();
    }

}
