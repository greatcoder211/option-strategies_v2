package ownStrategy.logic.mapper;

import org.mapstruct.Mapper;
import ownStrategy.dto.UserDTO;
import ownStrategy.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDTO dto);
    UserDTO toDto(User dto);
    List<UserDTO> toDtoList(List<User> dtoList);
}
