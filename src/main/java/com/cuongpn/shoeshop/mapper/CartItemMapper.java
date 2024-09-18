package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "productSize.product.title",target = "productName")
    @Mapping(source = "productSize.product.thumbnailImageUrl",target = "productImage")
    @Mapping(source = "productSize.size.value",target = "size")

    CartItemDTO cartItemToCartItemDTO(CartItem cartItem);
}
