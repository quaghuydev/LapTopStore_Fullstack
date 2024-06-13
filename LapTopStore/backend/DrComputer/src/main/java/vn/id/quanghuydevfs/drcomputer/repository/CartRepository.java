package vn.id.quanghuydevfs.drcomputer.repository;

import vn.id.quanghuydevfs.drcomputer.exception.NotEnoughProductsInStockException;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;

import java.math.BigDecimal;
import java.util.Map;

public interface CartRepository {

    void addProduct(Product product);

    void removeProduct(Product product);

    Map<Product, Integer> getProductsInCart();

    void checkout() throws NotEnoughProductsInStockException;

    BigDecimal getTotal();
}
