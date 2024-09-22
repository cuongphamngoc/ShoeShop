package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ProductService productService;
    @Value("${product.featured.num}")
    private int featuredProductNum;

    @GetMapping("/")
    public String getIndex(Model model){
        Page<Product> products = productService.findProductFeatured(featuredProductNum);
        model.addAttribute("products",products.getContent());
        return "index";
    }
}
