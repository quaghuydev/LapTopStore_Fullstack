package vn.id.quanghuydevfs.drcomputer.dto.product;

import lombok.Data;

import java.util.List;
@Data
public class DeleteProductRequest {
    private List<Long> productIds;
}
