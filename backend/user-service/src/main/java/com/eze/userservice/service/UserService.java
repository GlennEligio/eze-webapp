package com.eze.userservice.service;

import com.eze.userservice.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findUser(String username);
    User createUser(User user);
    User updateUser(User user);
    Boolean deleteUser(String username);
    User authenticateUser(String username, String password);
    Boolean addAvatar(String username, MultipartFile imageBytes) throws IOException;
}
