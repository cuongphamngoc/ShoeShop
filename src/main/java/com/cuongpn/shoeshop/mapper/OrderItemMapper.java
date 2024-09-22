package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.OrderItemDTO;
import com.cuongpn.shoeshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "productSize.product.title",target = "productName")
    @Mapping(source = "productSize.product.thumbnailImageUrl",target = "productImage")
    @Mapping(source = "productSize.size.value",target = "size")
    public OrderItemDTO orderItemToOrderItemDTO(OrderItem orderItem);
}
