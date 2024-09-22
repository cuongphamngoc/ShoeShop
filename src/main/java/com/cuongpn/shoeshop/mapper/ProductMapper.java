package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.*;
import com.cuongpn.shoeshop.entity.*;
import com.cuongpn.shoeshop.enums.ImageType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productImages", target = "galleryImages", qualifiedByName = "filterGalleryImages")
    @Mapping(source = "productImages", target = "detailsImages", qualifiedByName = "filterDetailImages")
    @Mapping(source = "productSizes", target = "sizes", qualifiedByName = "mapProductSizes")
    @Mapping(source = "brand",target = "brand",qualifiedByName = "mapBrand")
    @Mapping(source = "categories",target = "categories",qualifiedByName = "mapCategories")
    ProductDTO toProductDTO(Product product);

    @Named("filterGalleryImages")
    default List<ImageDTO> filterGalleryImages(Set<ProductImage> images) {
        return images.stream()
                .filter(image -> image.getImageType() == ImageType.GALLERY)
                .map(image -> new ImageDTO(image.getId(), image.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Named("filterDetailImages")
    default List<ImageDTO> filterDetailImages(Set<ProductImage> images) {
        return images.stream()
                .filter(image -> image.getImageType() != ImageType.GALLERY)
                .map(image -> new ImageDTO(image.getId(), image.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Named("mapProductSizes")
    default List<ProductSizeDTO> mapProductSizes(Set<ProductSize> productSizes) {
        return productSizes.stream()
                .map(proSize -> new ProductSizeDTO(proSize.getId().getProductId(), proSize.getId().getSizeId(), proSize.getSize().getValue(), proSize.getStock()))
                .sorted(Comparator.comparingLong(ProductSizeDTO::getValue))
                .collect(Collectors.toList());
    }
    @Named("mapBrand")
    default BrandDTO mapBrand(Brand brand){
        return new BrandDTO(brand.getId(),brand.getName());
    }
    @Named("mapCategories")
    default List<CategoryDTO> mapCategories(Set<Category> categories){
        return categories.stream().map(category -> new CategoryDTO(category.getId(),category.getName())).toList();
    }

    List<ProductDTO> toProductDTOs(List<Product> products);
}