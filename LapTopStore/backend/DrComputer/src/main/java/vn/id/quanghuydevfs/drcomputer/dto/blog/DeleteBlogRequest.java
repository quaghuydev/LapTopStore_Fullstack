package vn.id.quanghuydevfs.drcomputer.dto.blog;

import lombok.Data;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserDto;

import java.util.List;
@Data
public class DeleteBlogRequest {
    private List<Long> blogIds;
}
