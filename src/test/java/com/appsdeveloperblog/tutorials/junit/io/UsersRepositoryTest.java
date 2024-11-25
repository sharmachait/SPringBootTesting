package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsersRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UsersRepository usersRepository;
    UserEntity userEntity;
    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setEmail("john@doe.com");
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setEncryptedPassword("some encrypted password");
    }
    @Test
    void testFindByEmail_whenExistingEmail_returnsUser(){
        //arrange
        entityManager.persistAndFlush(userEntity);
        //act
        UserEntity savedEntity = usersRepository.findByEmail(userEntity.getEmail());
        //assert
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(userEntity.getEmail(), savedEntity.getEmail());
        assertEquals(userEntity.getEncryptedPassword(), savedEntity.getEncryptedPassword());
        assertEquals(userEntity.getUserId(), savedEntity.getUserId());
        assertEquals(userEntity.getFirstName(), savedEntity.getFirstName());
        assertEquals(userEntity.getLastName(), savedEntity.getLastName());
    }
    @Test
    void testFindByEmailEndsWith_whenExistingEmail_returnsUser(){
        //arrange
        entityManager.persistAndFlush(userEntity);
        //act
        List<UserEntity> savedEntity = usersRepository.findByEmailEndsWith(
                userEntity.getEmail().substring(
                        userEntity.getEmail().indexOf('@')
                )
        );
        //assert
        assertNotNull(savedEntity);
        assertTrue(savedEntity.size() > 0);
        assertTrue(savedEntity.contains(userEntity), "Returned list should contain the saved user");
        assertEquals(1, savedEntity.size(), "Should return exactly one user");
    }
    @Test
    void testFindByEmailEndsWithJPQL_whenExistingEmail_returnsUser(){
        //arrange
        entityManager.persistAndFlush(userEntity);
        //act
        List<UserEntity> savedEntity = usersRepository.findUsersWithEmalEndingWith(
                userEntity.getEmail().substring(
                        userEntity.getEmail().indexOf('@')
                )
        );
        //assert
        assertNotNull(savedEntity);
        assertTrue(savedEntity.size() > 0);
        assertTrue(savedEntity.contains(userEntity), "Returned list should contain the saved user");
        assertEquals(1, savedEntity.size(), "Should return exactly one user");
    }
    @Test
    void testFindByFirstNameOrLastName_whenExistingEmail_returnsUser(){
        //arrange
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setFirstName("John");
        userEntity1.setLastName("Sharma");
        userEntity1.setEmail("john@Sharma.com");
        userEntity1.setUserId(UUID.randomUUID().toString());
        userEntity1.setEncryptedPassword("some encrypted password");

        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(userEntity1);

        //act
        List<UserEntity> savedEntity = usersRepository.findByFirstNameOrLastName(
            "John", "Sharma"
        );
        //assert
        assertNotNull(savedEntity);
        assertTrue(savedEntity.size() > 1);
        assertTrue(savedEntity.contains(userEntity), "Returned list should contain the saved user");
        assertTrue(savedEntity.contains(userEntity1), "Returned list should contain the saved user");
        assertEquals(2, savedEntity.size(), "Should return exactly two user");
    }
}