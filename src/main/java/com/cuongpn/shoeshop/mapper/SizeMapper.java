package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.SizeDTO;
import com.cuongpn.shoeshop.entity.Size;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SizeMapper {
    SizeDTO sizeToSizeDTO(Size size);
}
