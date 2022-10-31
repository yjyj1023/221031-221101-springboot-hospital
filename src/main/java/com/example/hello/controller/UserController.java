package com.example.hello.controller;


import com.example.hello.dao.UserDao;
import com.example.hello.domain.user.User;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/user")
    public User addAndGet() throws SQLException {
        userDao.add(new User("1", "YeonJae", "1234"));
        return userDao.get("1");
    }


//    @DeleteMapping("/user")
//    public ResponseEntity<Integer> deleteAll() {
//        return ResponseEntity
//                .ok()
//                .body(userDao.deletAll());
//    }
}