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
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserServiceImpl implements UserService , UserDetailsService {
    public static final String USER_NOT_FOUND_BY_SPESIFIED_USER_NAME = "User not found by spesified userName ";
    public static final String RETURNING_FOUND_USER_BY_USER_NAME = "Returning found user by userName ";
    public static final String USERNAME_ALREADY_EXIST = "Username Already Exist ";
    public static final String EMAIL_ALREADY_EXIST = "Email Already Exist ";
    public static final String NO_USER_FOUND_BY_USER_NAME = "No User Found By User Name ";
    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  LoggingAttemptService loggingAttemptService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findUserByUserName(username);
        if(user==null){
            LOGGER.error(USER_NOT_FOUND_BY_SPESIFIED_USER_NAME +username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_SPESIFIED_USER_NAME +username);
        }else{
            try {
                validateLoggingAttempt(user);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            user.setLastLoginDate(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(RETURNING_FOUND_USER_BY_USER_NAME +username);
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
        user.setNonLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        userRepository.save(user);
        LOGGER.info(password +" trk");
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
            throw new UserNameExistException(USERNAME_ALREADY_EXIST + userByUsername);
        }
        User userByEmail = findUserByEmail(newEmail);
        if(userByEmail!=null) {
            throw new EmailExistException(EMAIL_ALREADY_EXIST + userByEmail);
        }
        if(StringUtils.hasText(currentUserName)){
            User currentUser = findUserByUsername(currentUserName);
            if(currentUser==null) throw new UsernameNotFoundException(NO_USER_FOUND_BY_USER_NAME +currentUserName);

            if(newUserName!=null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UserNameExistException(USERNAME_ALREADY_EXIST + userByUsername);
            }

            if( !currentUser.getId().equals(userByUsername.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST + userByEmail);
            }

            return currentUser;
        }else{
            return  null;
        }

    }

    private void validateLoggingAttempt(User user) throws ExecutionException {
        if(user.isNonLocked()){
            if(loggingAttemptService.hasExceededMaxAttempts(user.getUserName())){
                user.setNonLocked(false);
            }else{
                user.setNonLocked(true);
            }
        }else {
            loggingAttemptService.evictUserFromLoggingAttemptCache(user.getUserName());
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
