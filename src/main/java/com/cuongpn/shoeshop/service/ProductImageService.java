package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductImage;
import com.cuongpn.shoeshop.enums.ImageType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductImageService {
    public ProductImage addNewProductImage(String imgUrl, Product product, ImageType imageType);

    public void deleteProductImageByUrl(String url);

    public List<ProductImage> findByProductAndType(Product product, ImageType type);

    public ProductImage findByImageUrl(String url);

    public Optional<ProductImage> findById(Long id);

    public void deleteById(Long id);

    public void delete(ProductImage productImage);
}
