package com.cuongpn.shoeshop.service.Impl;

import com.cuongpn.shoeshop.domain.ProductSpecification;
import com.cuongpn.shoeshop.domain.SortFilter;
import com.cuongpn.shoeshop.dto.*;
import com.cuongpn.shoeshop.entity.*;
import com.cuongpn.shoeshop.enums.ImageType;
import com.cuongpn.shoeshop.mapper.ProductMapper;
import com.cuongpn.shoeshop.repository.*;
import com.cuongpn.shoeshop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final SizeService sizeService;
    private final ProductSizeService productSizeService;
    private final FileUpLoadService fileService;
    private final ProductImageService productImageService;
    private final ProductMapper productMapper;
    private static final String UPLOAD_FOLDER = "product";

    @Override
    public Page<Product> getAllProduct(ProductFilterForm productFilterForm) {
        int pageNo = productFilterForm.getPageNo();
        if(pageNo>= 1) pageNo = pageNo-1;
        Sort sort = SortFilter.getSort(productFilterForm.getSortType());
        Pageable pageable = PageRequest.of(pageNo, productFilterForm.getPageSize(), sort);
        Specification<Product> specification = ProductSpecification.buildFilter(productFilterForm.getSize(), productFilterForm.getCategory(), productFilterForm.getBrand(), productFilterForm.getSearchKey(), productFilterForm.getPriceLow(), productFilterForm.getPriceHigh());
        return productRepository.findAll(specification, pageable);
    }


    @Override
    @Cacheable(value="product", key="#id")
    public ProductDTO getProductDetail(Long id) {
        return getProductDto(id);
    }

    @Override
    @Transactional
    public String addNewProduct(AddProductForm addProductForm)  {
        Product newProduct = createNewProductFromForm(addProductForm);
        Product current = productRepository.save(newProduct);
        addProductSizes(current,addProductForm.getSizes());
        addProductImages(current,addProductForm);
        productRepository.save(current);
        return "Success";


    }
    private Product createNewProductFromForm(AddProductForm productForm){
        Brand brand = brandService.findById(productForm.getBrand());
        Set<Category> categories = categoryService.findAllById(productForm.getCategories());
        return Product.builder()
                .title(productForm.getTitle())
                .brand(brand)
                .categories(categories)
                .price(productForm.getPrice())
                .productImages(new HashSet<>())
                .productSizes(new HashSet<>())
                .thumbnailImageUrl("")
                .build();
    }
    private void addProductSizes(Product product, Map<Long,Integer> sizes){
        for (Map.Entry<Long, Integer> entry : sizes.entrySet()) {
            Size size = sizeService.findById(entry.getKey());
            ProductSize productSize = productSizeService.addProductSize(product, size, entry.getValue());
            size.getProductSizes().add(productSize);
            product.getProductSizes().add(productSize);
            sizeService.saveSize(size);
        }
    }
    private void addProductImages(Product product, AddProductForm productForm)  {
        Set<ProductImage> productImages = product.getProductImages();
        String thumbnailImageUrl = product.getThumbnailImageUrl();
        MultipartFile[] galleryImages = productForm.getGalleryImages();
        MultipartFile[] detailImages = productForm.getDetailImages();
        int galleryImagesLength = galleryImages.length;
        MultipartFile[] allImages = Stream.concat(Arrays.stream(galleryImages), Arrays.stream(detailImages)).toArray(MultipartFile[]::new);
        List<Map<String, String>> imageResponse = uploadMultipleFiles(allImages, UPLOAD_FOLDER);
        int count = 0;
        for (Map<String, String> image : imageResponse) {
            if (thumbnailImageUrl.isEmpty()) {
                thumbnailImageUrl = image.get("secure_url");

            }
            boolean isGalleryImage =  count < galleryImagesLength;
            ImageType imageType = isGalleryImage ? ImageType.GALLERY : ImageType.DETAIL;

            ProductImage productImage = productImageService.addNewProductImage(image.get("secure_url"), image.get("public_id"), product, imageType);
            productImages.add(productImage);
            count++;
        }
        product.setThumbnailImageUrl(thumbnailImageUrl);

    }


    @Override
    @Cacheable(value="product")
    public List<ProductDTO> getAllProductDTO() {
        List<Product> list = productRepository.findAll();
        return productMapper.toProductDTOs(list);
    }

    @Override
    @Cacheable(value="product", key="#id")
    public ProductDTO getProductDto(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        System.out.println(productMapper.toProductDTO(product));
        return productMapper.toProductDTO(product);
    }

    @Override
    public String deleteProduct(Long id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow();
        List<String> publicIds = product.getProductImages().stream().map(ProductImage::getPublicId).toList();
        fileService.destroyImages(publicIds);
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
                .categories(mapCategories(product))
                .sizes(mapSizes(product))
                .title(product.getTitle())
                .detailImagesDTO(mapImages(product, ImageType.DETAIL))
                .galleryImagesDTO(mapImages(product, ImageType.GALLERY))
                .build();
    }

    private Set<Long> mapCategories(Product product) {
        return product.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    private Map<Long, Integer> mapSizes(Product product) {
        return product.getProductSizes().stream()
                .collect(Collectors.toMap(
                        productSize -> productSize.getSize().getValue(),
                        ProductSize::getStock
                ));
    }

    private List<ImageDTO> mapImages(Product product, ImageType imageType) {
        return product.getProductImages().stream()
                .filter(productImage -> productImage.getImageType() == imageType)
                .map(productImage -> new ImageDTO(productImage.getId(), productImage.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveProduct(ProductForm productForm)  {
        Product product = productRepository.findById(productForm.getId()).orElseThrow();
        updateProductSizes(product, productForm);
        updateProductDetails(product, productForm);
        updateProductImages(product, productForm);
        productRepository.save(product);
    }

    @Override
    public Page<Product> findProductFeatured(int productFeaturedNum) {
        Pageable pageable = PageRequest.of(0,productFeaturedNum);
        return productRepository.findProductFeatured(pageable);
    }

    private void updateProductSizes(Product product, ProductForm productForm) {
        Set<ProductSize> productSizes = product.getProductSizes();
        productSizes.removeIf(productSize -> !productForm.getSizes().containsKey(productSize.getSize().getValue()));

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
    }

    private void updateProductDetails(Product product, ProductForm productForm) {
        product.setTitle(productForm.getTitle());
        Brand brand = brandService.findById(productForm.getBrand());
        product.setBrand(brand);
        product.setPrice(productForm.getPrice());
        product.setCategories(categoryService.findAllById(productForm.getCategories()));
    }

    private void updateProductImages(Product product, ProductForm productForm)  {
        Set<ProductImage> productImages = product.getProductImages();
        deleteImages(productImages, productForm.getGalleryImagesToDelete());
        deleteImages(productImages, productForm.getDetailImagesToDelete());

        uploadImages(product, productForm.getGalleryImages(), ImageType.GALLERY);
        uploadImages(product, productForm.getDetailImages(), ImageType.DETAIL);
    }

    private void deleteImages(Set<ProductImage> productImages, String imagesToDelete) {
        List<String> publicIds = Arrays.stream(imagesToDelete.split(","))
                .filter(id -> !id.isEmpty())
                .map(Long::parseLong)
                .map(productImageService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(productImages::remove)
                .map(ProductImage::getPublicId)
                .collect(Collectors.toList());

        // Delete from Cloudinary
        deleteImagesFromCloudinary(publicIds);

        // Delete from the database
        publicIds.forEach(productImageService::deleteByPublicId);
    }

    private void deleteImagesFromCloudinary(List<String> publicIds) {
        try {
            if(publicIds.isEmpty()) return;
            fileService.destroyImages(publicIds);

        } catch (Exception e) {
            // Handle exception appropriately
            log.error("Error deleting images from Cloudinary", e);
        }
    }


    private void uploadImages(Product product, MultipartFile[] images, ImageType imageType) {
        if(images.length == 0 ) return;
        List<MultipartFile> nonEmptyFiles = Arrays.stream(images)
                .filter(file -> !file.isEmpty())
                .toList();

        if (nonEmptyFiles.isEmpty()) return;
        List<Map<String,String>> uploadResults = this.uploadMultipleFiles(nonEmptyFiles.toArray(new MultipartFile[0]),UPLOAD_FOLDER);
        for (Map<String, String> result : uploadResults) {
            ProductImage productImage = productImageService.addNewProductImage(
                    result.get("secure_url"),
                    result.get("public_id"),
                    product,
                    imageType
            );
            product.getProductImages().add(productImage);
        }
    }

    public List<Map<String, String>> uploadMultipleFiles(MultipartFile[] multipartFiles, String folder)  {
        List<CompletableFuture<Map<String, Object>>> futures = Arrays.stream(multipartFiles)
                .map(file -> uploadFileAsync(file, folder))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<Map<String, String>>> allResults = allOf.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .map(this::mapResult)
                        .toList());

        return allResults.join();
    }

    private CompletableFuture<Map<String, Object>> uploadFileAsync(MultipartFile file, String folder) {
        try {
            return fileService.uploadFile(file, folder);
        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private Map<String, String> mapResult(Map<String, Object> result) {
        if (result != null) {
            Map<String, String> map = new HashMap<>();
            map.put("secure_url", (String) result.get("secure_url"));
            map.put("public_id", (String) result.get("public_id"));
            return map;
        } else {
            return new HashMap<>();
        }
    }


}
