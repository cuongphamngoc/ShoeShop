package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.UserDTO;
import com.cuongpn.shoeshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source="avatar",target = "avatar")
    UserDTO userToUserDTO(User user);
}
