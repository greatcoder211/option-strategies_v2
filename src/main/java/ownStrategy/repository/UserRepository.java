package ownStrategy.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ownStrategy.dto.UserDTO;
import ownStrategy.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    ResponseEntity<UserDTO> getUserById(String id);

    ResponseEntity<UserDTO> getUserByUsername(String username);
}
