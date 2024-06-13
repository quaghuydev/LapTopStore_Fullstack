package vn.id.quanghuydevfs.drcomputer.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class DeleteUserRequest {
    private List<Long> userIds;
}
