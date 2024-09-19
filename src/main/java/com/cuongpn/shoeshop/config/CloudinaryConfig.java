package com.cuongpn.shoeshop.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig  {
    @Value("${CLOUDINARY_URL}")
    private String CLOUDINARY_URL;
    @Bean
    public Cloudinary getCloudinary(){

        return new Cloudinary(CLOUDINARY_URL);
    }
}
