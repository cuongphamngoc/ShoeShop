package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.AddProductForm;
import com.cuongpn.shoeshop.dto.ProductForm;
import com.cuongpn.shoeshop.dto.ProductDTO;
import com.cuongpn.shoeshop.dto.ProductFilterForm;
import com.cuongpn.shoeshop.entity.Product;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Page<Product> getAllProduct(ProductFilterForm productFilterForm);

    ProductDTO getProductDetail(Long id);

    String addNewProduct(AddProductForm addProductForm) throws IOException;

    List<ProductDTO> getAllProductDTO();

    ProductDTO getProductDto(Long id);

    String deleteProduct(Long id) throws Exception;

    ProductForm getProductFormById(Long id);

    void saveProduct(ProductForm productForm) throws IOException;


    Page<Product> findProductFeatured(int productFeaturedNum);
}
