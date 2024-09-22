package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {

    void deleteByPublicId(String publicId);
}
