package com.cuongpn.shoeshop.dto;

import com.cuongpn.shoeshop.entity.Brand;
import com.cuongpn.shoeshop.entity.Category;
import com.cuongpn.shoeshop.entity.ProductSize;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class ProductDTO {
    private Long id;
    private String title;
    private String price;
    private List<Category> categories;
    private Brand brand;
    private List<ProductSizeDTO> sizes;
    private List<ImageDTO> galleryImages;
    private List<ImageDTO> detailsImages;
}
