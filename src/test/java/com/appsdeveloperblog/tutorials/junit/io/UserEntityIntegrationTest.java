package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserEntityIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;
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
    void testUserEntity_whenValidDetailsProvided_returnsStoredDetails(){
        //arrange
        //act
        UserEntity savedEntity = entityManager.persistAndFlush(userEntity);
        //assert
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertTrue(userEntity.getId()>0);
        assertEquals(userEntity.getFirstName(), savedEntity.getFirstName());
        assertEquals(userEntity.getLastName(), savedEntity.getLastName());
        assertEquals(userEntity.getEmail(), savedEntity.getEmail());
        assertEquals(userEntity.getUserId(), savedEntity.getUserId());
        assertEquals(userEntity.getEncryptedPassword(), savedEntity.getEncryptedPassword());
    }
    @Test
    void testUserEntity_whenFirstnameTooLong_throwsException(){
        //arrange
        userEntity.setFirstName("JohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohn");
        //assert
        assertThrows(PersistenceException.class, ()->{
            //act
            UserEntity savedEntity = entityManager.persistAndFlush(userEntity);
        });
    }
    @Test
    void testUserEntity_whenDuplicateUserId_throwsException(){
        //arrange
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setFirstName("John");
        userEntity1.setLastName("Doe");
        userEntity1.setEmail("john@doe.com");
        userEntity1.setUserId(userEntity.getUserId());
        userEntity1.setEncryptedPassword("some encrypted password");
        entityManager.persistAndFlush(userEntity);

        //assert
        assertThrows(PersistenceException.class, ()->{
            //act
            entityManager.persistAndFlush(userEntity1);
        });
    }
}