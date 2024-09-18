package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerAdvice {
    private final CartService shoppingCartService;
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
