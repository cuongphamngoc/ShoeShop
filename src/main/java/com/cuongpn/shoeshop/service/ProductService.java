package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.AddProductForm;
import com.cuongpn.shoeshop.dto.ProductForm;
import com.cuongpn.shoeshop.dto.ProductDTO;
import com.cuongpn.shoeshop.dto.ProductFilterForm;
import com.cuongpn.shoeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    public List<Product> getAllProduct();

    public Page<Product> getAllProduct(ProductFilterForm productFilterForm);

    public Product getProductDetail(@RequestParam("id") Long id, Model model);

    public String addNewProduct(AddProductForm addProductForm) throws IOException;

    public List<ProductDTO> getAllProductDTO();

    public ProductDTO getProductDto(Long id);

    public String deleteProduct(Long id) throws IOException;

    public ProductForm getProductFormById(Long id);

    public void saveProduct(ProductForm productForm) throws IOException;


}
