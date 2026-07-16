package ownStrategy.dto;
import java.util.Set;
public record UserDTO(String id,
                      String username,
                      String email,
                      String password,
                      Set<String> roles){}
