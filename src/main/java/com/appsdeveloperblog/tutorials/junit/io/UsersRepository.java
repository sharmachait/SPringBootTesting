package com.appsdeveloperblog.tutorials.junit.io;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    List<UserEntity> findByEmailEndsWith(String suffix);
    List<UserEntity> findByFirstNameOrLastName(String firstName, String lastName);
    @Query("select u from UserEntity u where u.email like %:emailDomain")
    List<UserEntity> findUsersWithEmalEndingWith(@Param("emailDomain") String emailDomain);
}