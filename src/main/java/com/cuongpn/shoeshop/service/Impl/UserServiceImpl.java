package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.*;
import com.cuongpn.shoeshop.entity.Address;
import com.cuongpn.shoeshop.entity.Product;
import com.cuongpn.shoeshop.entity.Role;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.repository.RoleRepository;
import com.cuongpn.shoeshop.repository.UserRepository;
import com.cuongpn.shoeshop.service.FileService;
import com.cuongpn.shoeshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FileService fileService;

    @Override
    public User createUser(String username, String password, String email, List<String> role) {
        Set<Role> roles = role.stream()
                .map(roleRepository::findByName)
                .collect(Collectors.toSet());
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .roles(roles)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User saveUser(SignUpForm form) {
        Role role = roleRepository.findByName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder()
                .username(form.getUsername())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .roles(roles)
                .build();
        return userRepository.save(user);
    }

    @Override
    public void ChangePassword(ChangePasswordForm changePasswordForm, Principal principal) {
        User currentUser = this.findByUserName(principal.getName());
        currentUser.setPassword(passwordEncoder.encode(changePasswordForm.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    public void saveAddress(Principal principal, AddressDTO addressDTO) {
        User currentUser = this.findByUserName(principal.getName());
        Address address = Address.builder().name(addressDTO.getName())
                .country("Viet Nam")
                .district(addressDTO.getDistrict())
                .city(addressDTO.getCity())
                .zipCode(addressDTO.getZipCode())
                .streetAddress(addressDTO.getStreetAddress())
                .user(currentUser)
                .build();
        currentUser.getAddress().add(address);
        userRepository.save(currentUser);
    }

    @Override
    public User findByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user name not found with " + username));
    }

    @Override
    public boolean isUserNameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public User saveNewProfile(UserDTO userDTO) {
        User existingUser = userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new RuntimeException("Username not found " + userDTO.getUsername()));
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());

        User res = userRepository.save(existingUser);
        updateUserDetailsInSession(res);
        return res;

    }

    @Override
    public String updateAvatar(MultipartFile multipartFile, Principal principal, HttpServletRequest request) {
        String fileUrl = fileService.uploadFile(multipartFile, request, principal);
        User existingUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Username not found " + principal.getName()));
        existingUser.setAvatar(fileUrl);
        userRepository.save(existingUser);
        return fileUrl;

    }



    private void updateUserDetailsInSession(User user) {


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }


}