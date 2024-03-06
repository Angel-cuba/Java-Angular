package com.example.demo.services;

import com.example.demo.models.Roles;
import com.example.demo.models.User;
import com.example.demo.models.dto.user.UserLoginRequest;
import com.example.demo.models.dto.user.UserRequest;
import com.example.demo.models.dto.user.UserResponse;
import com.example.demo.models.dto.user.UserUpdateRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Helpers;
import com.example.demo.utils.JwtHelper;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Helpers helpers;

    private final PasswordEncoder passwordEncoder;

    private final JwtHelper jwtHelper;

    private final AuthenticationManager authenticationManager;

    private static final String notFound = "User not found";

    @Autowired
    public UserService(UserRepository userRepository, Helpers helpers, PasswordEncoder passwordEncoder, JwtHelper jwtHelper, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.helpers = helpers;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
        this.authenticationManager = authenticationManager;
    }

    private boolean isUserValid(UserRequest user) {
        return user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty();
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
        User newUser = getUser(user);
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setImage(user.getImage() != null ? user.getImage() : "");
        newUser.setBio(user.getBio() != null ? user.getBio() : "");
        newUser.setGithub(user.getGithub() != null ? user.getGithub() : "");
        newUser.setLinkedin(user.getLinkedin() != null ? user.getLinkedin() : "");
        newUser.setRole(Roles.USER);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(newUser);

        String token = jwtHelper.generateToken(newUser);
        User userdata = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException(notFound));
        // Return a response with the token and the user data(Username, Image, Role)
        UserResponse userData = new UserResponse();
        userData.setId(userdata.getId());
        userData.setUsername(userdata.getUsername());
        userData.setImage(userdata.getImage());
        userData.setRole(userdata.getRole().name());
        return helpers.responseWithData("User signed in successfully", HttpStatus.OK, Map.of("token", token, "user", userData));
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

    public ResponseEntity<Map<String, Object>> signIn(UserLoginRequest user) {
        User userOptional = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException(notFound));
        if (!passwordEncoder.matches(user.getPassword(), userOptional.getPassword())) {
            return helpers.response("Invalid credentials", HttpStatus.BAD_REQUEST);
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        String token = jwtHelper.generateToken(userOptional);
        User userdata = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException(notFound));
        // Return a response with the token and the user data(Username, Image, Role)
        UserResponse userData = new UserResponse();
        userData.setId(userdata.getId());
        userData.setUsername(userdata.getUsername());
        userData.setImage(userdata.getImage());
        userData.setRole(userdata.getRole().name());
        return helpers.responseWithData("User signed in successfully", HttpStatus.OK, Map.of("token", token, "user", userData));
    }
}
