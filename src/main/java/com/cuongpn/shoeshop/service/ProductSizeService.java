package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.entity.Size;

import java.util.Optional;

public interface ProductSizeService {
    public ProductSize addProductSize(Product product, Size size, int Stock);

    public Optional<ProductSize> findById(Product product, Size size);
}
