package com.example.wealthwise_api.dtoMapper;

import com.example.wealthwise_api.DTO.UserDTO;
import com.example.wealthwise_api.Entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserEntityDTOMapper implements Function<UserEntity, UserDTO> {
    @Override
    public UserDTO apply(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getEmail(),
                userEntity.getRole()
        );
    }
}