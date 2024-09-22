package com.cuongpn.shoeshop.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface FileUpLoadService {

    Map<String,Object> uploadImage(MultipartFile multipartFile, String folder) throws IOException;

    CompletableFuture<Map<String,Object>> uploadFile(MultipartFile file, String folder) throws IOException ;


    void destroyImages(List<String> publicIds) throws Exception;


    }
