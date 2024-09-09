package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.dto.SignInForm;
import com.cuongpn.shoeshop.dto.SignUpForm;
import com.cuongpn.shoeshop.service.AuthService;
import com.cuongpn.shoeshop.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
@Controller
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/signin")
    public String getLoginForm(Model model){
        model.addAttribute("signin", new SignInForm());
        return "login";
    }
    @PostMapping("/signin")
    public String processSignin(@Valid @ModelAttribute("signin") SignInForm form, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "signin";
        }
        System.out.println(form);
        authService.login(form);
        return "redirect:/index";
    }
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignUpForm());
        return "signup";
    }
    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("signupForm") SignUpForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        userService.saveUser(form);
        return "redirect:/signin";
    }
    @GetMapping("/")
    public String getHome(){
        return "index";
    }
}
