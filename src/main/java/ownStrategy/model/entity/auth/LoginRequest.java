package ownStrategy.model.entity.auth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record LoginRequest (
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 20 characters")
    String username,
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 30, message = "Password mus contain 6-30 characters")
    String password
){}