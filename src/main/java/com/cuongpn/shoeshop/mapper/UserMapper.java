package com.cuongpn.shoeshop.mapper;

import com.cuongpn.shoeshop.dto.UserDTO;
import com.cuongpn.shoeshop.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);
}
