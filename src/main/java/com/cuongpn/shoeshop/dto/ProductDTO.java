package com.cuongpn.shoeshop.dto;

import com.cuongpn.shoeshop.entity.Brand;
import com.cuongpn.shoeshop.entity.Category;
import com.cuongpn.shoeshop.entity.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String price;
    private List<CategoryDTO> categories;
    private BrandDTO brand;
    private List<ProductSizeDTO> sizes;
    private List<ImageDTO> galleryImages;
    private List<ImageDTO> detailsImages;
}
