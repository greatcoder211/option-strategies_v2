package ownStrategy.controller.user;

public class UserContracts {
    public record ChangeKnownPasswordPasscodes(String userId, String oldPassword, String newPassword){}

    public record ChangeEmailPasscodes(String userId, String enteredPassword, String newEmail){}

    public record ChangeUsernamePasscodes(String userId, String enteredPassword, String newUsername){}
}