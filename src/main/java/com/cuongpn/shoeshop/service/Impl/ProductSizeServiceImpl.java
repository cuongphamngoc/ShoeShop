package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductSize;
import com.cuongpn.shoeshop.entity.ProductSizeId;
import com.cuongpn.shoeshop.entity.Size;
import com.cuongpn.shoeshop.repository.ProductSizeRepository;
import com.cuongpn.shoeshop.service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {
    private final ProductSizeRepository productSizeRepository;
    @Override
    public ProductSize addProductSize(Product product, Size size, int Stock) {
        ProductSize productSize = ProductSize.builder()
                .id(new ProductSizeId(product.getId(),size.getId()))
                .stock(Stock)
                .product(product)
                .size(size)
                .build();
        return productSizeRepository.save(productSize);
    }

    @Override
    public Optional<ProductSize> findById(Product product, Size size) {
        return productSizeRepository.findByIdOrderBySizeValue(new ProductSizeId(product.getId(),size.getId()));
    }
    public Optional<ProductSize> findById(Long productId, Long SizeId){
        return productSizeRepository.findByIdOrderBySizeValue(new ProductSizeId(productId,SizeId));

    }

    @Override
    public ProductSize saveProductSize(ProductSize productSize) {

        return productSizeRepository.save(productSize);
    }
}
