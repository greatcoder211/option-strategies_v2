package ownStrategy.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ownStrategy.dto.RegisterRequest;
import ownStrategy.dto.UserDTO;
import ownStrategy.model.User;
import ownStrategy.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

    @Service
    public class UserService {

        private final UserRepository userRepo;
        private final PasswordEncoder encoder;

        public UserService(UserRepository userRepo, PasswordEncoder encoder) {
            this.userRepo = userRepo;
            this.encoder = encoder;
        }

        public void register(RegisterRequest registerRequest) {
            if (userRepo.findByUsername(registerRequest.getUsername()).isPresent()) {
                throw new RuntimeException("Error: Username is already taken!");
            }
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(encoder.encode(registerRequest.getPassword()));
            userRepo.save(user);
        }

        public List<UserDTO> getAllUsers() {
            List<User> allUsers = userRepo.findAll();
            List<UserDTO> dtoList = new ArrayList<>();
            for (User user : allUsers) {
                dtoList.add(new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
            }
            return dtoList;
        }

        public UserDTO getUserById(String id) {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        }

        public UserDTO getUserByUsername(String username) {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        }
    }