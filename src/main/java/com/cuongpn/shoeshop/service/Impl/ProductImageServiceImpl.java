package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductImage;
import com.cuongpn.shoeshop.enums.ImageType;
import com.cuongpn.shoeshop.repository.ProductImageRepository;
import com.cuongpn.shoeshop.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    @Override
    @Transactional
    public ProductImage addNewProductImage(String imageUrl, String imgPublicId, Product product, ImageType imageType) {
        ProductImage productImage = ProductImage.builder()
                .publicId(imgPublicId)
                .imageUrl(imageUrl)
                .imageType(imageType)
                .product(product)
                .build();
        return productImageRepository.save(productImage);
    }

    @Override
    public Optional<ProductImage> findById(Long id) {
        return productImageRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteByPublicId(String publicId) {
        productImageRepository.deleteByPublicId(publicId);
    }

    @Override
    @Transactional
    public void delete(ProductImage productImage) {
        productImageRepository.delete(productImage);
    }
}
