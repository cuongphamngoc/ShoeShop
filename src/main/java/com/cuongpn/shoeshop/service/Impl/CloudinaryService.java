package com.cuongpn.shoeshop.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cuongpn.shoeshop.service.FileUpLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements FileUpLoadService {
    private final Cloudinary cloudinary;

    @Async
    @Override
    public Map<String,Object> uploadImage(MultipartFile multipartFile, String folder) throws IOException {
        return cloudinary.uploader().upload(multipartFile.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "use_filename", true,
                        "unique_filename", true));
    }


    @Async
    @Override
    public void destroyImages(List<String> publicIds) throws Exception {
        Map<String,Object> deleteResult =  cloudinary.api().deleteResources(publicIds,ObjectUtils.emptyMap());
    }
    @Override
    @Async
    public CompletableFuture<Map<String,Object>> uploadFile(MultipartFile file, String folder) throws IOException {
        Map<String,Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "use_filename", true,
                        "unique_filename", true));
        return CompletableFuture.completedFuture(uploadResult);
    }




}
