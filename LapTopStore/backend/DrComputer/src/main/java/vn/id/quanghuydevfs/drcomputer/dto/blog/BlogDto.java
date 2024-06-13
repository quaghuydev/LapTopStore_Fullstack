package vn.id.quanghuydevfs.drcomputer.dto.blog;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class BlogDto {
    private String title;
    private String category;
    private String description;
    private String author;
    private String content;
    private String img;
    private Date dataCreate;
}
