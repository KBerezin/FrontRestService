package ru.javamentor.Front.rest;

import ru.javamentor.Front.entity.User;
import ru.javamentor.Front.exception.UserNotFoundException;
import ru.javamentor.Front.response.UserSuccessResponse;
import ru.javamentor.Front.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api")
@Secured("ROLE_ADMIN")
public class UserRestController {
    private UserServiceImpl userServiceImpl;

    public UserRestController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping(value = "/users/list")
    public List<User> getUserList() {
        return userServiceImpl.listAll();
    }

    @GetMapping(value = "/users/{userId}")
    public User getUser(@PathVariable long userId) {
        if (userServiceImpl.get(userId) == null || (userId < 0)) {
            throw new UserNotFoundException("User id not found - " + userId);
        }
        return userServiceImpl.get(userId);
    }

    @PostMapping(value = "/users")
    public UserSuccessResponse addUser(@RequestBody User user) {
        user.setId(null);
        userServiceImpl.save(user);
        return new UserSuccessResponse(
                HttpStatus.OK.value(),
                "User " + user.getUsername() + " successfully added",
                System.currentTimeMillis()
        );
    }

    @PutMapping(value = "/users")
    public UserSuccessResponse updateUser(@RequestBody User user) {
        if (userServiceImpl.get(user.getId()) == null || (user.getId() < 0)) {
            throw new UserNotFoundException("User id not found - " + user.getId());
        }
        userServiceImpl.update(user);
        return new UserSuccessResponse(
                HttpStatus.OK.value(),
                "User successfully updated",
                System.currentTimeMillis()
        );
    }

    @DeleteMapping(value = "/users/{userId}")
    public UserSuccessResponse deleteUser(@PathVariable long userId) {
        if (userServiceImpl.get(userId) == null || (userId < 0)) {
            throw new UserNotFoundException("User id not found - " + userId);
        }
        userServiceImpl.delete(userId);
        return new UserSuccessResponse(
                HttpStatus.OK.value(),
                "User successfully deleted id - " + userId,
                System.currentTimeMillis()
        );
    }
}