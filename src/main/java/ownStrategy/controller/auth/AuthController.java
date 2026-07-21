package ownStrategy.controller.auth;
import jakarta.validation.Valid;
import ownStrategy.logic.mapper.AuthMapper;
import ownStrategy.model.User;
import ownStrategy.service.AuthService;
import ownStrategy.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;
    private final AuthMapper authMapper;

    public AuthController(CustomUserDetailsService userDetailsService, AuthService authService, AuthMapper authMapper) {
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.authMapper = authMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthContracts.RegisterRequestDTO registerRequestDto) {
        User user = authService.registerUser(authMapper.toEntity(registerRequestDto));
        final String jwt = authService.getJwt(user);
        return ResponseEntity.ok(new AuthContracts.AuthResponseDTO(jwt));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthContracts.LoginRequestDTO loginRequestDto) {
        authService.authenticateUser(authMapper.toEntity(loginRequestDto));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.username());
        final String jwt = authService.getJwt((User) userDetails);
        return ResponseEntity.ok(new AuthContracts.AuthResponseDTO(jwt));
    }
}