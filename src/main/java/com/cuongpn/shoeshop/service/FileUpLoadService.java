package com.cuongpn.shoeshop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface FileUpLoadService {

    public Map uploadImage(MultipartFile multipartFile, String folder) throws IOException;

    public List<Map<String, String>> uploadMultipleFiles(MultipartFile[] multipartFile, String folder) throws IOException;

    public Map destroyImage(String ImageUrl) throws IOException;


}
