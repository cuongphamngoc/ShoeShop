package com.cuongpn.shoeshop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

public interface FileService {
    public String uploadFile(MultipartFile multipartFile, HttpServletRequest request, Principal principal);

    public String uploadImage(MultipartFile multipartFile) throws IOException;
    public void init();
}
