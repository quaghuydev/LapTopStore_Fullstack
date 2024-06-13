package vn.id.quanghuydevfs.drcomputer.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantity {
    private Product product;
    private Long totalQuantity;
}
