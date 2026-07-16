package ownStrategy.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ownStrategy.dto.UserDTO;
import ownStrategy.dto.portfolio.PortfolioStrategyDTO;
import ownStrategy.logic.mapper.StrategyMapper;
import ownStrategy.logic.mapper.UserMapper;
import ownStrategy.service.strategy.StrategyService;
import ownStrategy.service.UserService;

import java.util.List;

//zarządzanie użytkownikami
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final StrategyService strategyService;
    private final StrategyMapper strategyMapper;
    private final UserMapper userMapper;

    public UserController(UserService userService, StrategyService strategyService, StrategyMapper strategyMapper, UserMapper userMapper) {
        this.userService = userService;
        this.strategyService = strategyService;
        this.strategyMapper = strategyMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userMapper.toDtoList(userService.getAllUsers()));
    }

    @GetMapping("/portfolio/get/{userId}")
    public List<PortfolioStrategyDTO> getUserPortfolio(@PathVariable String userId) {
        return strategyMapper.toDtoList(strategyService.getPortfolioByUserId(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userMapper.toDto(userService.getUserById(userId)));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userMapper.toDto(userService.getUserByUsername(username)));
    }

    @PostMapping("/add")
    public void addUser(@RequestBody UserDTO user) {
        userService.addUser(user);
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

    @DeleteMapping("/portfolio/delete/{userId}")
    public void deleteUserPortfolioById(@PathVariable String userId) {
        strategyService.deleteUserPortfolio(userId);
    }

    @GetMapping("admins/all")
    public List<UserDTO> getAllAdmins() {
        return userMapper.toDtoList(userService.getAllAdmins());
    }

    @GetMapping("regulars/all")
    public List<UserDTO> getAllRegularUsers() {
        return userMapper.toDtoList(userService.getAllRegularUsers());
    }

    @GetMapping("change/password/{userId}")
    public void changeKnownPassword(
            @RequestBody UserContracts.ChangeKnownPasswordPasscodes passcodes){
        userService.changeKnownPassword(passcodes.userId(), passcodes.oldPassword(), passcodes.newPassword());
    }

    @GetMapping("change/email/{userId}")
    public void changeEmail(
            @RequestBody UserContracts.ChangeEmailPasscodes passcodes){
        userService.changeKnownPassword(passcodes.userId(), passcodes.enteredPassword(), passcodes.newEmail());
    }

    @GetMapping("change/username/{userId}")
    public void changeUsername(
            @RequestBody UserContracts.ChangeUsernamePasscodes passcodes){
        userService.changeUsername(passcodes.userId(), passcodes.enteredPassword(), passcodes.newUsername());
    }
}
                //usunięte metody:
// - checkUser(znajdź użytkownika o konkretnym id)
