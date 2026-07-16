package ownStrategy.logic.mapper;

import org.mapstruct.Mapper;
import ownStrategy.controller.auth.AuthContracts;
import ownStrategy.model.entity.auth.LoginRequest;
import ownStrategy.model.entity.auth.RegisterRequest;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    RegisterRequest toEntity(AuthContracts.RegisterRequestDTO dto);
    LoginRequest toEntity(AuthContracts.LoginRequestDTO dto);
}
