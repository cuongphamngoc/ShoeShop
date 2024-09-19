package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.domain.ProductSpecification;
import com.cuongpn.shoeshop.domain.SortFilter;
import com.cuongpn.shoeshop.dto.*;
import com.cuongpn.shoeshop.entity.*;
import com.cuongpn.shoeshop.enums.ImageType;
import com.cuongpn.shoeshop.repository.*;
import com.cuongpn.shoeshop.service.*;
import com.cuongpn.shoeshop.util.CurrencyUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final SizeService sizeService;
    private final ProductSizeService productSizeService;
    private final FileUpLoadService fileService;
    private final ProductImageService productImageService;

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProduct(ProductFilterForm productFilterForm) {
        Sort sort = SortFilter.getSort(productFilterForm.getSortType());
        Pageable pageable = PageRequest.of(productFilterForm.getPageNo(), productFilterForm.getPageSize(), sort);
        Specification<Product> specification = ProductSpecification.buildFilter(productFilterForm.getSize(),productFilterForm.getCategory(),productFilterForm.getBrand(),productFilterForm.getSearchKey(),productFilterForm.getPriceLow(),productFilterForm.getPriceHigh());
        return productRepository.findAll(specification,pageable);


    }



    @Override
    public Product getProductDetail(Long id, Model model) {
        return productRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public String addNewProduct(AddProductForm addProductForm) throws IOException {
        long startTime = System.nanoTime();

        Brand brand = brandService.findById(addProductForm.getBrand());
        Set<Category> categorySet = categoryService.findAllById(addProductForm.getCategories());
        Product product = Product.builder()
                .title(addProductForm.getTitle())
                .price(addProductForm.getPrice())
                .brand(brand)
                .categories(categorySet)
                .build();
        Product current = productRepository.save(product);
        if(current.getProductSizes()== null){
            current.setProductSizes(new HashSet<>());
        }
        if(current.getProductImages() == null){
            current.setProductImages(new HashSet<>());
        }
        for(Map.Entry<Long, Integer> entry : addProductForm.getSizes().entrySet()){
            Size  size = sizeService.findById(entry.getKey());
            ProductSize productSize = productSizeService.addProductSize(current,size,entry.getValue());
            size.getProductSizes().add(productSize);
            current.getProductSizes().add(productSize);
            sizeService.saveSize(size);
        }
        Set<ProductImage> productImages = new HashSet<>();
        String thumbnail = "";
        String folder = "product";

        MultipartFile [] galleryImages  = addProductForm.getGalleryImages();
        List<Map<String,String>> imageResponse = fileService.uploadMultipleFiles(galleryImages,folder);
        for(Map<String,String> image : imageResponse){
            if(thumbnail.isEmpty()){
                thumbnail= image.get("secure_url");

            }
            ProductImage productImage = productImageService.addNewProductImage(image.get("secure_url"),image.get("public_id"),current,ImageType.GALLERY);
            productImages.add(productImage);
        }
        /*for(MultipartFile images : addProductForm.getGalleryImages()){
            Map map  = fileService.uploadImage(images,folder);
            if(thumbnail.isEmpty()){
                thumbnail= map.get("secure_url").toString();

            }
            ProductImage productImage = productImageService.addNewProductImage(map.get("secure_url").toString(),map.get("public_id").toString(),current,ImageType.GALLERY);
            productImages.add(productImage);
        }*/
        MultipartFile [] detailImages  = addProductForm.getDetailImages();
        List<Map<String,String>> imageResponse2 = fileService.uploadMultipleFiles(detailImages,folder);
        for(Map<String,String> image : imageResponse2){
            ProductImage productImage = productImageService.addNewProductImage(image.get("secure_url"),image.get("public_id"),current,ImageType.DETAIL);
            productImages.add(productImage);
        }
//        for(MultipartFile images : addProductForm.getDetailImages()){
//            Map map  = fileService.uploadImage(images,folder);
//            ProductImage productImage = productImageService.addNewProductImage(map.get("secure_url").toString(),map.get("public_id").toString(),current,ImageType.DETAIL);
//            productImages.add(productImage);
//        }
        current.setThumbnailImageUrl(thumbnail);
        current.getProductImages().addAll(productImages);
        productRepository.save(current);
        long endTime = System.nanoTime();

        // Tính toán thời gian thực thi
        long duration = (endTime - startTime); // Đơn vị: nano giây

        System.out.println("Thời gian thực thi: " + duration + " nano giây");
        return "Success";


    }

    @Override
    public List<ProductDTO> getAllProductDTO() {
        List<Product> list = productRepository.findAll();

        Map<Long,List<ImageDTO>> galleryMap= new HashMap<>();
        Map<Long,List<ImageDTO>> detailMap= new HashMap<>();
        list.forEach(product->{
            galleryMap.put(product.getId(),new ArrayList<>());
            detailMap.put(product.getId(), new ArrayList<>());
        });


        list.forEach(product ->
                product.getProductImages().stream()
                        .filter(image -> image.getImageType() == ImageType.GALLERY)
                        .forEach(image -> galleryMap.get(product.getId()).add(new ImageDTO(image.getId(), image.getImageUrl())))
        );
        list.forEach(product ->
                product.getProductImages().stream()
                        .filter(image -> image.getImageType() != ImageType.GALLERY)
                        .forEach(image -> detailMap.get(product.getId()).add(new ImageDTO(image.getId(), image.getImageUrl())))
        );

        return (List<ProductDTO>) list.stream().map(product->{
                    List<ProductSizeDTO> productSizeDTOList = new ArrayList<>(product.getProductSizes().stream().map(proSize ->
                            new ProductSizeDTO(proSize.getId().getProductId(), proSize.getId().getSizeId(), proSize.getSize().getValue(), proSize.getStock())).toList());
                    productSizeDTOList.sort((a,b)->Long.compare(a.getValue(),b.getValue()));
                    return ProductDTO.builder().id(product.getId())
                            .brand(product.getBrand())
                            .title(product.getTitle())
                            .price(CurrencyUtil.formatCurrency(product.getPrice()))
                            .categories(product.getCategories().stream().toList())
                            .sizes(productSizeDTOList)
                            .galleryImages(galleryMap.get(product.getId()))
                            .detailsImages(detailMap.get(product.getId()))
                            .build();
                }


        ).toList();
    }

    @Override
    public ProductDTO getProductDto(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        List<ImageDTO> galleryImages = new ArrayList<>();
        List<ImageDTO> detailImages = new ArrayList<>();
        product.getProductImages().forEach(
                image->
                {
                    if(image.getImageType() == ImageType.GALLERY){
                        galleryImages.add(new ImageDTO(image.getId(),image.getImageUrl()));
                    }
                    else{
                        detailImages.add(new ImageDTO(image.getId(),image.getImageUrl()));
                    }
                }
        );
        galleryImages.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        detailImages.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        List<ProductSizeDTO> sizeDTOS = new ArrayList<>(product.getProductSizes().stream().map(size ->
                new ProductSizeDTO(size.getId().getProductId(), size.getId().getSizeId(), size.getSize().getValue(), size.getStock())
        ).toList());
        sizeDTOS.sort((a,b)->Long.compare(a.getValue(),b.getValue()));
        return ProductDTO.builder()
                .id(product.getId())
                .price(CurrencyUtil.formatCurrency(product.getPrice()))
                .title(product.getTitle())
                .brand(product.getBrand())
                .sizes(sizeDTOS)
                .categories(product.getCategories().stream().toList())
                .galleryImages(galleryImages)
                .detailsImages(detailImages)
                .build();

    }

    @Override
    public String deleteProduct(Long id) throws IOException {
        Product product = productRepository.findById(id).orElseThrow();
        Set<ProductImage> productImages = product.getProductImages();
        for(ProductImage productImage: productImages){
            Map res = fileService.destroyImage(productImage.getPublic_id());
        }
        productRepository.deleteById(id);
        return "success";
    }

    @Override
    public ProductForm getProductFormById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return ProductForm.builder()
                .id(product.getId())
                .brand(product.getBrand().getId())
                .price(product.getPrice())
                .categories(product.getCategories().stream().map(Category::getId).collect(Collectors.toSet()))
                .sizes(product.getProductSizes().stream().collect(Collectors.toMap(productsize-> productsize.getSize().getValue(),ProductSize::getStock)))
                .title(product.getTitle())
                .detailImagesDTO(product.getProductImages().stream().filter(productImage -> productImage.getImageType()==ImageType.DETAIL).map(productImage -> new ImageDTO(productImage.getId(),productImage.getImageUrl())).toList())
                .galleryImagesDTO(product.getProductImages().stream().filter(productImage -> productImage.getImageType()==ImageType.GALLERY).map(productImage -> new ImageDTO(productImage.getId(),productImage.getImageUrl())).toList())
                .build();
    }

    @Override
    public void saveProduct(ProductForm productForm) throws IOException {
        Product product = productRepository.findById(productForm.getId()).orElseThrow();
        Set<ProductSize> productSizes = product.getProductSizes();
        productSizes.removeIf(productSize ->
                !productForm.getSizes().containsKey(productSize.getSize().getValue())
        );
        productForm.getSizes().forEach((value, stock) -> {
            Size size = sizeService.findByValue(value);
            Optional<ProductSize> optionalProductSize = productSizeService.findById(product, size);

            if (optionalProductSize.isPresent()) {
                ProductSize productSize = optionalProductSize.get();
                productSize.setStock(stock);
            } else {
                ProductSize newProductSize = productSizeService.addProductSize(product, size, stock);
                productSizes.add(newProductSize);
            }
        });

        product.setTitle(productForm.getTitle());
        Brand brand = brandService.findById(productForm.getBrand());
        product.setBrand(brand);
        product.setPrice(productForm.getPrice());
        product.setCategories(categoryService.findAllById(productForm.getCategories()));
        Set<ProductImage> productImages = product.getProductImages();
        System.out.println(productForm.getGalleryImagesToDelete());
        System.out.println(productForm.getDetailImagesToDelete());
        for(String url: productForm.getGalleryImagesToDelete().split(",")){
            System.out.println("Url search..." + url);
            if(url.isEmpty()) continue;
            Optional<ProductImage> optionalProductImage = productImageService.findById(Long.parseLong(url));
            System.out.println(optionalProductImage);
            if (optionalProductImage.isPresent()) {
                System.out.println("Removing gallery image: " + url);
                ProductImage productImage = optionalProductImage.get();
                productImages.remove(productImage);
                productImageService.delete(productImage);
            }
        }
        System.out.println(productForm.getDetailImagesToDelete());
        for(String url: productForm.getDetailImagesToDelete().split(",")){
            System.out.println("Url search..." + url);
            if(url.isEmpty()) continue;
            Optional<ProductImage> optionalProductImage = productImageService.findById(Long.parseLong(url));
            System.out.println(optionalProductImage);
            if (optionalProductImage.isPresent()) {
                ProductImage productImage = optionalProductImage.get();
                System.out.println("Removing gallery image: " + url);
                productImages.remove(productImage);
                productImageService.delete(productImage);
            }
        }
        Set<ProductImage> galleryProductImageSet = new HashSet<>(productImageService.findByProductAndType(product, ImageType.GALLERY));
        Set<ProductImage> detailProductImageSet = new HashSet<>(productImageService.findByProductAndType(product, ImageType.DETAIL));
        String folder = "product";
        for(MultipartFile multipartFile:productForm.getGalleryImages()){
            if(!multipartFile.isEmpty()){
                Map response  = fileService.uploadImage(multipartFile,folder);

                ProductImage productImage = productImageService.addNewProductImage(response.get("secure_url").toString(),response.get("public_id").toString(),product,ImageType.GALLERY);
                galleryProductImageSet.add(productImage);
            }


        }
        for(MultipartFile multipartFile:productForm.getDetailImages()){
            if(!multipartFile.isEmpty()){
                Map response  = fileService.uploadImage(multipartFile,folder);
                ProductImage productImage = productImageService.addNewProductImage(response.get("secure_url").toString(),response.get("public_id").toString(),product,ImageType.DETAIL);
                detailProductImageSet.add(productImage);
            }
        }
        productImages.addAll(galleryProductImageSet);
        productImages.addAll(detailProductImageSet);
        ProductImage thumbnail = galleryProductImageSet.stream().min(Comparator.comparingLong(ProductImage::getId)).get();
        product.setThumbnailImageUrl(thumbnail.getImageUrl());
        productRepository.save(product);

    }


}
