package vn.id.quanghuydevfs.drcomputer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import vn.id.quanghuydevfs.drcomputer.exception.NotEnoughProductsInStockException;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.repository.CartRepository;
import vn.id.quanghuydevfs.drcomputer.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class CartService implements CartRepository {
    @Autowired
    private final ProductRepository productRepository;
    private final Map<Product, Integer> products = new HashMap<>();

    @Override
    public void addProduct(Product product) {
        if (products.containsKey(product)) {
            products.replace(product, products.get(product) + 1);
        } else {
            products.put(product, 1);
        }
    }

    @Override
    public void removeProduct(Product product) {
        if (products.containsKey(product)) {
            if (products.get(product) > 1)
                products.replace(product, products.get(product) - 1);
            else if (products.get(product) == 1) {
                products.remove(product);
            }
        }
    }

    @Override
    public Map<Product, Integer> getProductsInCart() {
        return Collections.unmodifiableMap(products);
    }

    @Override
    public void checkout() throws NotEnoughProductsInStockException {
        Product product;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            product = productRepository.findById(entry.getKey().getId()).orElseThrow();
            if (product.getStorage() < entry.getValue())
                throw new NotEnoughProductsInStockException(product);
            entry.getKey().setStorage(product.getStorage() - entry.getValue());
        }
        products.clear();
    }

    @Override
    public BigDecimal getTotal() {
        return products.entrySet().stream()
                .map(entry -> {
                    BigDecimal productPrice = BigDecimal.valueOf(entry.getKey().getPrice() - entry.getKey().getPrice() * entry.getKey().getSale());
                    return productPrice.multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
