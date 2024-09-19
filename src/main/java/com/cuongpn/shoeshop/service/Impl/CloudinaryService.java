package com.cuongpn.shoeshop.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cuongpn.shoeshop.service.FileUpLoadService;
import com.luciad.imageio.webp.WebPReadParam;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CloudinaryService implements FileUpLoadService {
    private final Cloudinary cloudinary;

    @Override
    public Map uploadImage(MultipartFile multipartFile, String folder) throws IOException {
        return cloudinary.uploader().upload(multipartFile.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "use_filename", true,
                        "unique_filename", true));
    }

    @Override
    public List<Map<String, String>> uploadMultipleFiles(MultipartFile[] multipartFile, String folder) throws IOException {
        List<CompletableFuture<Map<String, Object>>> futures = Arrays.stream(multipartFile).map(
                file -> {
                    try {
                        return uploadFile(file,folder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<Map<String,String>>> allResults = allOf.thenApply(v ->
            futures.stream()
                    .map(CompletableFuture::join)
                    .map(result -> {
                        if (result != null) {
                            Map<String, String> map = new HashMap<>();
                            map.put("secure_url", (String) result.get("secure_url"));
                            map.put("public_id", (String) result.get("public_id"));
                            return map;
                        } else {
                            return new HashMap<String,String>();
                        }
                    }).toList());
        return allResults.join();


    }


    @Override
    public Map destroyImage(String publicId) throws IOException {

        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    @Async
    public CompletableFuture<Map<String,Object>> uploadFile(MultipartFile file, String folder) throws IOException {
        byte[] compressedImage = compressImage(file);
        Map<String,Object> uploadResult = cloudinary.uploader().upload(compressedImage,
                ObjectUtils.asMap("folder", folder));
        return CompletableFuture.completedFuture(uploadResult);
    }
    private byte[] compressImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage image = readWebPImage(file);
        Thumbnails.of(image)
                .size(800, 800)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }
    private BufferedImage readWebPImage(MultipartFile file) throws IOException {
        ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream());
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("webp");
        ImageReader reader = readers.next();
        reader.setInput(input, true);
        WebPReadParam param = new WebPReadParam();
        return reader.read(0, param);
    }



}
