package ownStrategy.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ownStrategy.model.entity.auth.LoginRequest;
import ownStrategy.model.entity.auth.RegisterRequest;
import ownStrategy.model.User;
import ownStrategy.repository.UserRepository;
import ownStrategy.security.JwtUtils;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        User newUser = new User(registerRequest.username(), registerRequest.email(), passwordEncoder.encode(registerRequest.password()));
        try{
            return userRepository.save(newUser);
        }
        catch (Exception e){
            System.out.println("Registration failed for email: " + newUser.getEmail());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getJwt(User user) {
        return jwtUtils.generateToken(user);
    }

    public void authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );
    }
}
