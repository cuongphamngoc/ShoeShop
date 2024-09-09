package com.cuongpn.shoeshop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Data
public class AddProductForm {
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
    @NotEmpty
    @Size(min = 1,message = "Gallery images must have at least 1 image")
    private MultipartFile[] galleryImages;
    @NotEmpty
    @Size(min = 1,message = "Detail images must have at least 1 image")
    private MultipartFile[] detailImages;
}
