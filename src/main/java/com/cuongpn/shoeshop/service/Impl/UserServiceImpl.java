package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.dto.*;
import com.cuongpn.shoeshop.entity.Address;
import com.cuongpn.shoeshop.entity.Role;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.repository.RoleRepository;
import com.cuongpn.shoeshop.repository.UserRepository;
import com.cuongpn.shoeshop.service.FileUpLoadService;
import com.cuongpn.shoeshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FileUpLoadService fileService;
    private static final String UPLOAD_FOLDER = "avatar";

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
    public void removeAddress(User user, Long id) {
        Set<Address> addressSet = user.getAddress();
        Address address =  addressSet.stream()
                .filter(address1 -> !address1.getIsDeleted())
                .filter(addr -> Objects.equals(addr.getId(), id))
                .findFirst().orElseThrow(()-> new RuntimeException("Address not found with id "+ id));
        address.setIsDeleted(true);
        userRepository.save(user);
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
        Set<Address> addressSet = currentUser.getAddress();
        Address address = addressSet.stream()
                .filter(addr-> Objects.equals(addr.getId(), addressDTO.getId()))
                .findFirst()
                .orElseGet(Address::new);

        address.setName(addressDTO.getName());
        address.setCountry("Viet Nam");
        address.setDistrict(addressDTO.getDistrict());
        address.setCity(addressDTO.getCity());
        address.setZipCode(addressDTO.getZipCode());
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setUser(currentUser);
        address.setIsDeleted(false);

        // Nếu địa chỉ mới, thêm vào tập hợp
        if (address.getId() == null) {
            addressSet.add(address);
        }
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
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        User res = userRepository.save(existingUser);
        updateUserDetailsInSession(res);
        return res;

    }

    @Override
    @Transactional
    public String updateAvatar(MultipartFile multipartFile, Principal principal, HttpServletRequest request) throws IOException {

        CompletableFuture<Map<String,Object>> response = fileService.uploadFile(multipartFile,UPLOAD_FOLDER);
        User existingUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Username not found " + principal.getName()));
        response.thenAccept(map->{

            existingUser.setAvatar(map.get("secure_url").toString());
            existingUser.setAvatar_public_id(map.get("public_id").toString());
            userRepository.save(existingUser);
        }).join();


        return existingUser.getAvatar();

    }

    @Override
    public void changePassword(ChangePasswordForm changePasswordForm, Principal principal) {
        String newPassword = changePasswordForm.getNewPassword();
        User user = this.findByUserName(principal.getName());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }


    private void updateUserDetailsInSession(User user) {


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }


}
