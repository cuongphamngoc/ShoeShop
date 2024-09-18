package com.cuongpn.shoeshop.service;

import com.cuongpn.shoeshop.dto.*;
import com.cuongpn.shoeshop.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService   {

    public User createUser(String username, String password, String email, List<String> role);

    public User saveUser(SignUpForm form);

    public void ChangePassword(ChangePasswordForm changePasswordForm, Principal principal);

    public void saveAddress(Principal principal, AddressDTO address);

    public User findByUserName(String username);

    public boolean isUserNameExists(String username);

    public boolean isEmailExists(String email);

    public User saveNewProfile(UserDTO userDTO);

    public String updateAvatar(MultipartFile multipartFile, Principal principal, HttpServletRequest request);

    public void changePassword(ChangePasswordForm changePasswordForm, Principal principal);

}
