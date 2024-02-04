package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.models.dto.user.UserRequest;
import com.example.demo.models.dto.user.UserUpdateRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Helpers;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Helpers helpers;

    @Autowired
    public UserService(UserRepository userRepository, Helpers helpers) {
        this.userRepository = userRepository;
        this.helpers = helpers;
    }

    String notFound = "User not found";
    
    private boolean isUserValid(UserRequest user) {
        return user.getEmail() == null || user.getEmail().isEmpty() || user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty();
    }
    
    private boolean isUserValidForUpdate(UserUpdateRequest user) {
        return user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty();
    }

    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserRequest user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            return helpers.response("Email already exists", HttpStatus.BAD_REQUEST);
        }
        if (isUserValid(user)) {
            return helpers.response("Invalid user details", HttpStatus.BAD_REQUEST);
        }
        // Create a new instance of the User class and set the properties of the user object
        final User newUser = getUser(user);

        userRepository.save(newUser);
        return helpers.response("User created successfully", HttpStatus.CREATED);
    }

    @NotNull
    private static User getUser(UserRequest user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setImage(user.getImage() != null ? user.getImage() : "");
        newUser.setBio(user.getBio() != null ? user.getBio() : "");
        newUser.setGithub(user.getGithub() != null ? user.getGithub() : "");
        newUser.setLinkedin(user.getLinkedin() != null ? user.getLinkedin() : "");
        return newUser;
    }

    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return helpers.responseWithData("Users retrieved successfully", HttpStatus.OK, userRepository.findAll());
    }

    public ResponseEntity<Map<String, Object>> getUserById(ObjectId id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("User retrieved successfully", HttpStatus.OK, user);
    }

    public ResponseEntity<Map<String, Object>> updateUser(ObjectId id, UserUpdateRequest user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }

        if (isUserValidForUpdate(user)) {
            return helpers.response("Invalid user details", HttpStatus.BAD_REQUEST);
        }

        User userToUpdate = userOptional.get();
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setImage(user.getImage() != null ? user.getImage() : "");
        userToUpdate.setBio(user.getBio() != null ? user.getBio() : "");
        userToUpdate.setGithub(user.getGithub() != null ? user.getGithub() : "");
        userToUpdate.setLinkedin(user.getLinkedin() != null ? user.getLinkedin() : "");
        userRepository.save(userToUpdate);
        return helpers.responseWithData("User updated successfully", HttpStatus.OK, userToUpdate);
    }

    public ResponseEntity<Map<String, Object>> deleteUser(ObjectId id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return helpers.response("User deleted successfully", HttpStatus.OK);
    }


}
