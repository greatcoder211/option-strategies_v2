package ownStrategy.controller.auth;

public class AuthContracts {
    public record LoginRequestDTO(String username, String password) {}
    public record RegisterRequestDTO(String username, String email, String password) {}
    public record AuthResponseDTO(String token) {}
}