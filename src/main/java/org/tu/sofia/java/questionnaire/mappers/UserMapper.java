package org.tu.sofia.java.questionnaire.mappers;

import org.tu.sofia.java.questionnaire.dtos.UserDTO;
import org.tu.sofia.java.questionnaire.entities.UserEntity;

public class UserMapper {
    // From entity to DTO
    public static UserDTO toDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new UserDTO(userEntity.getId(), userEntity.getUsername());
    }
    // From DTO to entity
    public static UserEntity toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDTO.getId());
        userEntity.setUsername(userDTO.getUsername());
        return userEntity;
    }
}