package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.ProductImage;
import com.cuongpn.shoeshop.enums.ImageType;
import com.cuongpn.shoeshop.repository.ProductImageRepository;
import com.cuongpn.shoeshop.service.ProductImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    @Override
    public ProductImage addNewProductImage(String imageUrl, String imgPublicId, Product product, ImageType imageType) {
        ProductImage productImage = ProductImage.builder()
                .public_id(imgPublicId)
                .imageUrl(imageUrl)
                .imageType(imageType)
                .product(product)
                .build();
        return productImageRepository.save(productImage);
    }

    @Override
    public void deleteProductImageByUrl(String url) {
        ProductImage productImage = productImageRepository.findByImageUrl(url);
        if(productImage != null)
        productImageRepository.delete(productImage);
    }

    @Override
    public List<ProductImage> findByProductAndType(Product product, ImageType type) {
        return productImageRepository.findByProductAndImageTypeOrderById(product,type);
    }

    @Override
    public ProductImage findByImageUrl(String url) {
        return productImageRepository.findByImageUrl(url);
    }

    @Override
    public Optional<ProductImage> findById(Long id) {
        return productImageRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public void delete(ProductImage productImage) {
        productImageRepository.delete(productImage);
    }
}
