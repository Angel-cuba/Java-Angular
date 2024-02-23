package com.example.demo.controllers;

import com.example.demo.models.dto.user.UserLoginRequest;
import com.example.demo.models.dto.user.UserRequest;
import com.example.demo.models.dto.user.UserUpdateRequest;
import com.example.demo.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable ObjectId userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserRequest user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable ObjectId id, @RequestBody UserUpdateRequest user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable ObjectId id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/signing")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody UserLoginRequest user) {
        return userService.signIn(user);
    }

}
