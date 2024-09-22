package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductImage;
import com.cuongpn.shoeshop.enums.ImageType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductImageService {
    ProductImage addNewProductImage(String imgUrl, String imgPublicId, Product product, ImageType imageType);

    Optional<ProductImage> findById(Long id);

    void deleteByPublicId(String publicId);

    void delete(ProductImage productImage);
}
