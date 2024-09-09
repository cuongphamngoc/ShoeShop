package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductImage;
import com.cuongpn.shoeshop.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {

    public ProductImage findByImageUrl(String url);

    public List<ProductImage> findByProductAndImageTypeOrderById(Product product, ImageType type);
}
