package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.ShoeShopApplication;
import com.cuongpn.shoeshop.exception.StorageException;
import com.cuongpn.shoeshop.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Objects;

@Service

public class FileServiceImpl implements FileService {

    private String avatarUploadDir;


    @Override
    public  String uploadFile(MultipartFile multipartFile, HttpServletRequest req, Principal principal) {
        init();
        try{
            if(multipartFile.isEmpty()){
                throw new StorageException("Failed to store empty file.");
            }
            String username = principal.getName();
            String filename = username + "_" + multipartFile.getOriginalFilename();
            String filePath = avatarUploadDir + filename;
            System.out.println(filePath);
            File destinationFile = new File(filePath);
            multipartFile.transferTo(destinationFile);


            String res =  "/image/avatar/" + filename;
            System.out.println(res);
            return res;

        }
        catch (IOException e){
            throw  new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile multipartFile) throws IOException {
        String imageUploadDir = System.getProperty("user.dir") + "/src/main/resources/static/image/product/";
        if(multipartFile.isEmpty()){
            throw new StorageException("Failed to store empty file.");
        }

        String filename = multipartFile.getOriginalFilename();
        System.out.println(filename);
        String filePath = imageUploadDir + filename;
        System.out.println(filePath);
        File destinationFile = new File(filePath);
        multipartFile.transferTo(destinationFile);


        String res =  "/image/product/" + filename;
        System.out.println(res);
        return res;
    }

    @Override
    public void init() {
        if(avatarUploadDir == null){
            avatarUploadDir = System.getProperty("user.dir") + "/src/main/resources/static/image/avatar/";
        }
    }


}
