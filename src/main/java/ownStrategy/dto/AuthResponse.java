package ownStrategy.dto;

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    // // Gettery i settery są potrzebne, by Spring mógł zamienić to na JSON
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}