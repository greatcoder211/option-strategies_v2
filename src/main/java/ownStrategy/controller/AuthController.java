package ownStrategy.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import ownStrategy.dto.LoginRequest;
import ownStrategy.dto.AuthResponse;
import ownStrategy.dto.RegisterRequest;
import ownStrategy.dto.UserDTO;
import ownStrategy.model.User;
import ownStrategy.repository.UserRepository;
import ownStrategy.security.JwtUtils;
import ownStrategy.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtUtils jwtUtils, UserRepository userRepo, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepo.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        userRepo.save(user);
        final String jwt = jwtUtils.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    @GetMapping("/api/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> allUsers = userRepo.findAll();
        List<UserDTO> safeUsersList = new ArrayList<>();
        for (User user : allUsers) {
            UserDTO dto = new UserDTO(user.getId(), user.getUsername(), user.getEmail());
            safeUsersList.add(dto);
        }
        return ResponseEntity.ok(safeUsersList);
    }
}