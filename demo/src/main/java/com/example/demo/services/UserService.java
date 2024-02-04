package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.models.dto.user.UserRequest;
import com.example.demo.models.dto.user.UserUpdateRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Helpers;
import org.bson.types.ObjectId;
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

    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserRequest user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User with email " + user.getEmail() + " already exists"));
        }
        // Create a new instance of the User class and set the properties of the user object
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setImage(user.getImage());
        newUser.setBio(user.getBio());
        newUser.setGithub(user.getGithub());
        newUser.setLinkedin(user.getLinkedin());

        userRepository.save(newUser);
        return helpers.response("User created successfully", HttpStatus.CREATED);
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
        User updatedUser = userOptional.get();
        updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setImage(user.getImage());
        updatedUser.setBio(user.getBio());
        updatedUser.setGithub(user.getGithub());
        updatedUser.setLinkedin(user.getLinkedin());

        userRepository.save(updatedUser);
        return helpers.responseWithData("User updated successfully", HttpStatus.OK, updatedUser);
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
