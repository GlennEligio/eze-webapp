package com.eze.userservice.repository;

import static org.junit.jupiter.api.Assertions.*;
import com.eze.userservice.domain.Role;
import com.eze.userservice.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("User Repository")
public class UserRepositoryTest{

    private TestEntityManager testEntityManager;

    private UserRepository userRepository;

    private User user1, user2;

    @Autowired
    public UserRepositoryTest(TestEntityManager testEntityManager, UserRepository userRepository) {
        this.testEntityManager = testEntityManager;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setup(){
        user1 = new User("name1", "username1", "password1", false, Role.USER);
        user2 = new User("name2", "username2", "password2", true, Role.ADMIN);
        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
    }

    @Test
    @DisplayName("returns a User using Valid Username")
    void findUser_withValidUsername_returnsUsers(){
        // Arrange
        String validUsername = "username1";
        // Act
        Optional<User> userOp = userRepository.findByUsernameAndDeleteFlagFalse(validUsername);

        // Assert
        assertTrue(userOp.isPresent());
        assertEquals(validUsername, userOp.get().getUsername());
    }

    @Test
    @DisplayName("throws an exception using Invalid Username")
    void findUser_withInvalidUsername_returnNoUser(){
        // Arrange
        String invalidUsername = "invalidUsername";
        // Act
        Optional<User> userOp = userRepository.findByUsernameAndDeleteFlagFalse(invalidUsername);

        // Assert
        assertTrue(userOp.isEmpty());
    }

    @Test
    @DisplayName("returns list of Users whose deleteFlag is false")
    void findAllUser_withUsersWithDeleteFlagFalse_returnsNotEmptyUserList(){
        List<User> users = userRepository.findByDeleteFlagFalse();

        assertNotEquals(0, users.size());
        assertNotEquals(0, (int) users.stream().filter(user -> user.getDeleteFlag().equals(false)).count());
    }

    @Test
    @DisplayName("changes User's deleteFlag to true when soft deleted")
    void softDelete_withValidUser_shouldChangeUserDeleteFlag(){
        String username = "username1";

        userRepository.softDelete(username);
        Optional<User> userOp = userRepository.findByUsernameAndDeleteFlagFalse(username);

        assertTrue(userOp.isEmpty());
    }
}
