package com.eze.userservice.service;

import com.eze.userservice.domain.User;
import com.eze.userservice.exception.ApiException;
import com.eze.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findByDeleteFlagFalse();
    }

    @Override
    public User findUser(String username) {
        List<User> users = repository.findByUsername(username);
        if(users.isEmpty()){
            throw new ApiException("No user with username " + username + " was found", HttpStatus.NOT_FOUND);
        }
        return users.get(0);
    }

    @Override
    public User createUser(User user) {
        List<User> users = repository.findByUsername(user.getUsername());
        if(!users.isEmpty()){
            throw new ApiException("User with username " + user.getUsername() + " already exist", HttpStatus.BAD_REQUEST);
        }
        user.setDeleteFlag(false);
        log.info(user.toString());
        return repository.save(user);
    }

    @Override
    public void updateUser(User user) {
        List<User> users = repository.findByUsername(user.getUsername());
        if(users.isEmpty()){
            throw new ApiException("User with username " + user.getUsername() + " does not exist", HttpStatus.NOT_FOUND);
        }
        User updatedUser = users.get(0);
        updatedUser.setName(user.getName());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setRole(user.getRole());
        repository.save(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        List<User> users = repository.findByUsername(username);
        if(users.isEmpty()){
            throw new ApiException("User with username " + username + " does not exist", HttpStatus.NOT_FOUND);
        }
        repository.softDelete(username);
    }
}
