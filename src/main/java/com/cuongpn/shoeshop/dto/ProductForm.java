package com.cuongpn.shoeshop.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Currency;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class ProductForm {
    @NotNull
    private Long id;
    @NotBlank(message = "Title must not blank")
    private String title;
    @Min(value = 0,message = "Price must greater than or equals to 0")
    @NotNull(message = "Price must not null")
    private Long price;
    @NotEmpty
    @NotNull
    @Size(min = 1,message = "Categories must have at least one element")
    private Set<Long> categories;
    @NotNull(message = "Brand must not null")
    private Long brand;
    @NotEmpty(message = "Sizes must have at least one element")
    private Map<Long, Integer> sizes;
    private MultipartFile[] galleryImages;
    private MultipartFile[] detailImages;
    private List<ImageDTO > galleryImagesDTO;

    private List<ImageDTO> detailImagesDTO;

    private String galleryImagesToDelete;
    private String detailImagesToDelete;

}
