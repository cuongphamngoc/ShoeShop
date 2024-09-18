package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.CartDTO;
import com.cuongpn.shoeshop.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO cartToCartDTO(Cart cart);
}
