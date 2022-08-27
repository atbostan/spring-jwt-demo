package com.bossware.jwtdemoapp.application.services.impl;

import com.bossware.jwtdemoapp.core.enums.Role;
import com.bossware.jwtdemoapp.core.exception.domain.email.EmailExistException;
import com.bossware.jwtdemoapp.core.exception.domain.user.UserNameExistException;
import com.bossware.jwtdemoapp.core.models.entities.User;
import com.bossware.jwtdemoapp.core.security.UserPrincipal;
import com.bossware.jwtdemoapp.application.repositories.UserRepository;
import com.bossware.jwtdemoapp.application.services.UserService;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserServiceImpl implements UserService , UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findUserByUserName(username);
        if(user==null){
            LOGGER.error("User not found by spesified userName "+username);
            throw new UsernameNotFoundException("User not found by spesified userName "+username);
        }else{
            user.setLastLoginDate(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found user by userName "+username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String userName, String email) throws UserNameExistException,EmailExistException,UsernameNotFoundException {
        validateUserNameAndEmail("",userName,email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setLocked(false);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        userRepository.save(user);

        return user;
    }

    private String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return  RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.random(10);
    }

    private User validateUserNameAndEmail(String currentUserName,String newUserName , String newEmail) throws UserNameExistException,EmailExistException,UsernameNotFoundException{
        User userByUsername = findUserByUsername(newUserName);
        if(userByUsername!=null){
            throw new UserNameExistException("Username Already Exist " + userByUsername);
        }
        User userByEmail = findUserByEmail(newEmail);
        if(userByEmail!=null) {
            throw new EmailExistException("Email Already Exist " + userByEmail);
        }
        if(StringUtils.hasText(currentUserName)){
            User currentUser = findUserByUsername(currentUserName);
            if(currentUser==null) throw new UsernameNotFoundException("No User Found By User Name "+currentUserName);

            if(newUserName!=null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UserNameExistException("Username Already Exist " + userByUsername);
            }

            if( !currentUser.getId().equals(userByUsername.getId())) {
                throw new EmailExistException("Email Already Exist " + userByEmail);
            }

            return currentUser;
        }else{
            return  null;
        }

    }
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String userName) {
        return userRepository.findUserByUserName(userName);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
