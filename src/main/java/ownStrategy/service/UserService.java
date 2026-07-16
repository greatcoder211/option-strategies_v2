package ownStrategy.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.dto.UserDTO;
import ownStrategy.logic.mapper.UserMapper;
import ownStrategy.model.User;
import ownStrategy.repository.UserRepository;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public List<User> getAllAdmins() {
        return (userRepository.findAll()
                .stream()
                .filter(user -> user.getRoles().contains("ROLE_ADMIN"))
                .toList());
    }

    public List<User> getAllRegularUsers() {
        return ((userRepository.findAll()
                .stream()
                .filter(user -> !user.getRoles().contains("ROLE_ADMIN"))
                .toList()));
    }

    //zostawiam, ale podejrzewam tu bardziej zaawansowaną logikę, szczególnie taka "zabawa hasłami" jest niebezpieczna
    public void addUser(UserDTO userDto) {
        if (!userDto.email().contains("@")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email must contain '@'");
        } else if (!userDto.email().toLowerCase().equals(userDto.email())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email cannot contain capital letters");
        }
        userRepository.save(userMapper.toEntity(userDto));
    }

    public void deleteUser(String userId) {
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    public void changeKnownPassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        if (passwordEncoder.encode(oldPassword).equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else throw new IllegalArgumentException("Wrong password. Try again.");
    }

    public void changeEmail(String userId, String enteredPassword, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        if(passwordEncoder.encode(enteredPassword).equals(user.getPassword())) {
            user.setEmail(newEmail);
            userRepository.save(user);
        }
        else throw new IllegalArgumentException("Wrong password. Try again.");
    }

    public void changeUsername(String userId, String enteredPassword, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        if(passwordEncoder.encode(enteredPassword).equals(user.getPassword())) {
            user.setUsername(newUsername);
            userRepository.save(user);
        }
        else throw new IllegalArgumentException("Wrong password. Try again.");
    }
}
