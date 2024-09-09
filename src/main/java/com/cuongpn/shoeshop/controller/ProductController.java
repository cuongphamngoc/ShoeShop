package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.dto.AddProductForm;
import com.cuongpn.shoeshop.dto.ProductForm;
import com.cuongpn.shoeshop.dto.ProductDTO;
import com.cuongpn.shoeshop.dto.ProductFilterForm;
import com.cuongpn.shoeshop.entity.Brand;
import com.cuongpn.shoeshop.entity.Category;
import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.Size;
import com.cuongpn.shoeshop.mapper.SizeMapper;
import com.cuongpn.shoeshop.service.BrandService;
import com.cuongpn.shoeshop.service.CategoryService;
import com.cuongpn.shoeshop.service.ProductService;
import com.cuongpn.shoeshop.service.SizeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SizeService sizeService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final SizeMapper sizeMapper;

    @GetMapping("/product")
    public String getAllByFilter( @ModelAttribute("filters") ProductFilterForm filterForm, Model model){

        Page<Product>productPage = productService.getAllProduct(filterForm);
        System.out.println(productPage.getTotalElements());
        model.addAttribute("productList",productPage.getContent());
        model.addAttribute("totalPage",productPage.getTotalPages());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("allSizes",sizeService.getAll().stream().map(Size::getValue).toList());
        model.addAttribute("allBrands",brandService.getAll().stream().map(Brand::getName).toList());
        model.addAttribute("allCategories",categoryService.getAll().stream().map(Category::getName).toList());
        model.addAttribute("filterForm",filterForm);
        model.addAttribute("itemsPerPage", 10);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String getProductDetails(@PathVariable("id") Long id, Model model){
        ProductDTO product = productService.getProductDto(id);
        model.addAttribute("product",product);
        return "product-detail";
    }
    @GetMapping("/product/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("addProductForm", new AddProductForm());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("sizes", sizeService.getAll().stream().map(sizeMapper::sizeToSizeDTO).toList());

        return "add-product";
    }
    @PostMapping("/product/add")
    public String addProduct(@Valid @ModelAttribute("addProductForm")
                                 AddProductForm addProductForm,
                             BindingResult bindingResult,
                             Model model,
                             Principal principal
                            ) throws IOException {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors().toString());
            model.addAttribute("categories", categoryService.getAll());
            model.addAttribute("brands", brandService.getAll());
            model.addAttribute("sizes", sizeService.getAll().stream().map(sizeMapper::sizeToSizeDTO).toList());
            return "add-product";
        }
        System.out.println(addProductForm);
        System.out.println(productService.addNewProduct(addProductForm));
        return "redirect:/product/list";
    }
    @GetMapping("/product/list")
    public String getProductList(Model model){
        List<ProductDTO> list = productService.getAllProductDTO();
        model.addAttribute("products",list);
        return "product-list";
    }
    @DeleteMapping("/product/{id}")
    public @ResponseBody String deleteProduct(@PathVariable(value = "id",required = true) Long id){
        return productService.deleteProduct(id);
    }
    @GetMapping("/product/edit/{id}")
    public String getEditProductForm(@PathVariable(value = "id") Long id,Model model){
        ProductForm productForm = productService.getProductFormById(id);
        model.addAttribute("productForm",productForm);
        model.addAttribute("allSizes",sizeService.getAll().stream().map(sizeMapper::sizeToSizeDTO).toList());
        model.addAttribute("allCategories",categoryService.getAll());
        model.addAttribute("allBrands",brandService.getAll());
        return "edit-product";
    }
    @PostMapping("/product/save")
    public String saveEditProduct(@ModelAttribute("productForm") ProductForm productForm,Model model,BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()){
            model.addAttribute("productForm",productForm);
            model.addAttribute("allSizes",sizeService.getAll().stream().map(sizeMapper::sizeToSizeDTO).toList());
            model.addAttribute("allCategories",categoryService.getAll());
            model.addAttribute("allBrands",brandService.getAll());
        }
        productService.saveProduct(productForm);
        return "redirect:/product/list";
    }

}