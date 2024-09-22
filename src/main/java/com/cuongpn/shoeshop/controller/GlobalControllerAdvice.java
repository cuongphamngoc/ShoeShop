package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final CartService shoppingCartService;
    private final ProductService productService;

    @ModelAttribute
    public void populateModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            User user =  (User) auth.getPrincipal();
            if (user != null) {
                model.addAttribute("shoppingCartItemNumber", shoppingCartService.getItemsNumber(user) );
            }
        } else {
            model.addAttribute("shoppingCartItemNumber", 0);
        }

    }
}
