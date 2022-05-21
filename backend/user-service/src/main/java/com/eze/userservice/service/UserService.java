package com.eze.userservice.service;

import com.eze.userservice.domain.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findUser(String username);
    User createUser(User user);
    void updateUser(User user);
    void deleteUser(String username);
    User authenticateUser(String username, String password);
}
