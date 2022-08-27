package com.bossware.jwtdemoapp.application.repositories;

import com.bossware.jwtdemoapp.core.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUserName(String userName);
    User findUserByEmail(String userName);
}
