package com.eze.userservice.service;

import com.eze.userservice.config.EzeUserDetails;
import com.eze.userservice.domain.User;
import com.eze.userservice.exception.ApiException;
import com.eze.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findByDeleteFlagFalse();
    }

    @Override
    public User findUser(String username) {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(username);
        return userOp.orElseThrow(() -> new ApiException("No user with username " + username + " was found", HttpStatus.NOT_FOUND));
    }

    @Override
    public User createUser(User user) {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(user.getUsername());
        if(userOp.isPresent()){
            throw new ApiException("User with username " + user.getUsername() + " already exist", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDeleteFlag(false);
        return repository.save(user);
    }

    @Override
    public User updateUser(User user) {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(user.getUsername());
        if(userOp.isEmpty()){
            throw new ApiException("User with username " + user.getUsername() + " does not exist", HttpStatus.NOT_FOUND);
        }
        User updatedUser = userOp.get();
        updatedUser.setName(user.getName());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setRole(user.getRole());
        return repository.save(updatedUser);
    }

    @Transactional
    @Override
    public Boolean deleteUser(String username) {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(username);
        if(userOp.isEmpty()){
            throw new ApiException("User with username " + username + " does not exist", HttpStatus.NOT_FOUND);
        }
        repository.softDelete(username);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(username);
        return new EzeUserDetails(userOp.orElseThrow(() -> new ApiException("User not authenticated", HttpStatus.UNAUTHORIZED)));
    }

    @Override
    public User authenticateUser(String username, String password) {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(username);
        if(userOp.isEmpty()){
            throw new ApiException("User with username " + username + " does not exist", HttpStatus.NOT_FOUND);
        }
        if(!passwordEncoder.matches(password, userOp.get().getPassword())){
            throw new ApiException("Incorrect username/password", HttpStatus.UNAUTHORIZED);
        }
        return userOp.get();
    }

    // TODO: Add Unit test
    @Override
    public Boolean addAvatar(String username, MultipartFile avatar) throws IOException {
        Optional<User> userOp = repository.findByUsernameAndDeleteFlagFalse(username);
        if(userOp.isEmpty()){
            throw new ApiException("User with username " + username + " does not exist", HttpStatus.NOT_FOUND);
        }

        Pattern pattern = Pattern.compile("\\.(jpeg|png|jpg)$");
        Matcher matcher = pattern.matcher(Objects.requireNonNull(avatar.getOriginalFilename()));
        if(!matcher.find()) {
            throw new ApiException("File must be either .jpeg, .jpg, or .png", HttpStatus.BAD_REQUEST);
        }
        if(avatar.getSize() > 1048577) {
            throw new ApiException("File must be less than 1mb", HttpStatus.BAD_REQUEST);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageIO.read(avatar.getInputStream());
        ImageIO.write(bufferedImage, "jpg", outputStream);

        User user = userOp.get();
        user.setAvatar(outputStream.toByteArray());
        repository.save(user);
        return true;
    }
}
