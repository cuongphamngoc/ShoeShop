package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.entity.ProductSizeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, ProductSizeId> {
    Optional<ProductSize> findByIdOrderBySizeValue(ProductSizeId id);
}
