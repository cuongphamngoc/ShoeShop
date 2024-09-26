package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "productSize.product.title",target = "productName")
    @Mapping(source = "productSize.product.thumbnailImageUrl",target = "productImage")
    @Mapping(source = "productSize.size.value",target = "size")

    CartItemDTO toCartItemDTO(CartItem cartItem);
    List<CartItemDTO> toCartItemDTOs(List<CartItem> cartItems);
}
