package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.dto.AddressDTO;
import com.cuongpn.shoeshop.dto.ChangePasswordForm;
import com.cuongpn.shoeshop.dto.UserDTO;
import com.cuongpn.shoeshop.entity.Address;
import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.mapper.UserMapper;
import com.cuongpn.shoeshop.service.OrderService;
import com.cuongpn.shoeshop.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final UserMapper userMapper;

    @GetMapping("/my-profile")
    public String getUserProfile(Model model, Principal principal){
        String userName =  principal.getName();
        User user = userService.findByUserName(userName);
        model.addAttribute("user", userMapper.userToUserDTO(user));
        System.out.println(userMapper.userToUserDTO(user));
        return "manageProfile";
    }
    @GetMapping("/edit-profile")
    public String getEditProfile(Model model, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        UserDTO userDTO= userMapper.userToUserDTO(user);
        model.addAttribute("user", userDTO);
        return "editProfile";
    }
    @PostMapping("/update-profile")
    public String postUserProfile(@Valid @ModelAttribute("user") UserDTO userDTO,BindingResult bindingResult,
                                 Model model, Principal principal){
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDTO);
            return "editProfile";
        }
        User user = userService.saveNewProfile(userDTO);
        model.addAttribute("user",userMapper.userToUserDTO(user));
        model.addAttribute("message","Profile updated successfully");

        return "redirect:/user/my-profile";


    }
    @GetMapping("/my-orders")
    public String myOrders(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);
        return "myOrders";
    }

    @GetMapping("/my-address")
    public String myAddress(Model model, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        System.out.println(user.getAddress());
        model.addAttribute("addresses", user.getAddress());
        return "manageAddress";
    }
    @GetMapping("/add-address")
    public String getAddressForm(Model model, Principal principal) {
        AddressDTO address = new AddressDTO();
        model.addAttribute("address", address);
        return "add-address";
    }
    @PostMapping("/add-address")
    public String getAddressForm(@Valid @ModelAttribute("address") AddressDTO address, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "add-address";
        }
        System.out.println(address);
        userService.saveAddress(principal,address);

        return "redirect:/user/my-address";
    }


    @GetMapping("/change-password")
    public String getChangePassword(Model model, Principal principal){

        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        model.addAttribute("password",changePasswordForm);
        return "changePassword";
    }
    @PostMapping("/change-password")
    public String updatePassword(Model model, @Valid @ModelAttribute("password")  ChangePasswordForm changePasswordForm,BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()) {
            model.addAttribute("password",changePasswordForm);
            return "changePassword";
        }
        userService.changePassword(changePasswordForm,principal);


        return "redirect:/user/my-profile";
    }
    @PostMapping("/change-avatar")
    public @ResponseBody Map<String, String> changeAvatar(@RequestParam("avatar")  MultipartFile multipartFile, Principal principal, HttpServletRequest request){
        String fileUrl = userService.updateAvatar(multipartFile, principal, request);

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);

        return response;


    }

}
