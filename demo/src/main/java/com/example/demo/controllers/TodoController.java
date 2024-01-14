package com.example.demo.controllers;

import com.example.demo.models.dto.todos.TodoCompletedRequest;
import com.example.demo.models.dto.todos.TodoRequest;
import com.example.demo.models.dto.todos.TodoUpdateRequest;
import com.example.demo.services.TodoService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTodoById(@PathVariable ObjectId id) {
        return todoService.getTodoById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTodo(@RequestBody TodoRequest todo) {
        return todoService.createTodo(todo);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateTodoById(@PathVariable ObjectId id, @RequestBody TodoUpdateRequest todo) {
        return todoService.updateTodoById(id, todo);
    }

    @PutMapping("/update/{id}/completed")
    public ResponseEntity<Map<String, Object>> updateTodoCompletedById(@PathVariable ObjectId id, @RequestBody TodoCompletedRequest todo) {
        return todoService.updateTodoCompletedById(id, todo);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTodoById(@PathVariable ObjectId id) {
        return todoService.deleteTodoById(id);
    }
}
