package com.example.demo.services;

import com.example.demo.models.Todo;
import com.example.demo.models.dto.todos.TodoCompletedRequest;
import com.example.demo.models.dto.todos.TodoRequest;
import com.example.demo.models.dto.todos.TodoUpdateRequest;
import com.example.demo.repository.TodoRepository;
import com.example.demo.utils.Helpers;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final Helpers helpers;

    @Autowired
    public TodoService(TodoRepository todoRepository, Helpers helpers) {
        this.todoRepository = todoRepository;
        this.helpers = helpers;
    }

    String notFound = "Todo not found";

    private boolean isTodoValid(@RequestBody TodoRequest todo) {
        return todo.getTitle() == null || todo.getTitle().isEmpty() || todo.getBody() == null || todo.getBody().isEmpty();
    }

    private boolean isTodoValidForUpdate(@RequestBody TodoUpdateRequest todo) {
        return todo.getTitle() == null || todo.getTitle().isEmpty() || todo.getBody() == null || todo.getBody().isEmpty() || todo.getEndDate() == null;
    }

    public ResponseEntity<Map<String, Object>> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        return helpers.responseWithData("Todos retrieved successfully", HttpStatus.OK, todos);
    }

    public ResponseEntity<Map<String, Object>> getTodoById(@PathVariable ObjectId id) {
        if(!todoRepository.existsById(id)) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        return helpers.responseWithData("Todo retrieved successfully", HttpStatus.OK, todoRepository.findById(id));
    }

    public ResponseEntity<Map<String, Object>> createTodo(@RequestBody TodoRequest todo) {
        if (isTodoValid(todo)) {
            return helpers.response("Invalid todo", HttpStatus.BAD_REQUEST);
        }
        Todo newTodo = new Todo();
        newTodo.setTitle(todo.getTitle());
        newTodo.setBody(todo.getBody());
        newTodo.setCompleted(false);
        newTodo.setEndDate(todo.getEndDate());
        newTodo.setCreatedAt(LocalDateTime.now());
        todoRepository.save(newTodo);
        return helpers.responseWithData("Todo created successfully", HttpStatus.OK, newTodo);
    }

    public ResponseEntity<Map<String, Object>> updateTodoById(@PathVariable ObjectId id, @RequestBody TodoUpdateRequest todo) {
       Optional<Todo> todoData = todoRepository.findById(id);
         if(todoData.isEmpty()) {
              return helpers.response(notFound, HttpStatus.NOT_FOUND);
         }
            if (isTodoValidForUpdate(todo)) {
                return helpers.response("Invalid todo", HttpStatus.BAD_REQUEST);
            }
            Todo todoToBeUpdated = todoData.get();
            todoToBeUpdated.setTitle(todo.getTitle());
            todoToBeUpdated.setBody(todo.getBody());
            todoToBeUpdated.setEndDate(todo.getEndDate());
            todoToBeUpdated.setCompleted(todo.getCompleted());
            todoToBeUpdated.setUpdatedAt(LocalDateTime.now());
            todoRepository.save(todoToBeUpdated);
            return helpers.responseWithData("Todo updated successfully", HttpStatus.OK, todoToBeUpdated);
    }

    public ResponseEntity<Map<String, Object>> updateTodoCompletedById(@PathVariable ObjectId id, @RequestBody TodoCompletedRequest todo) {
        Optional<Todo> todoData = todoRepository.findById(id);
        if(todoData.isEmpty()) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        Todo todoToBeUpdated = todoData.get();
        todoToBeUpdated.setCompleted(todo.getCompleted());
        todoRepository.save(todoToBeUpdated);
        return helpers.responseWithData("Todo updated successfully", HttpStatus.OK, todoToBeUpdated);
    }

    public ResponseEntity<Map<String, Object>> deleteTodoById(@PathVariable ObjectId id) {
        if(!todoRepository.existsById(id)) {
            return helpers.response(notFound, HttpStatus.NOT_FOUND);
        }
        todoRepository.deleteById(id);
        return helpers.response("Todo deleted successfully", HttpStatus.OK);
    }
}
