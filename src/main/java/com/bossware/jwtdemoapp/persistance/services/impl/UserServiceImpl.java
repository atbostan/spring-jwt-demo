package com.bossware.jwtdemoapp.persistance.services.impl;

import com.bossware.jwtdemoapp.core.models.entities.User;
import com.bossware.jwtdemoapp.core.security.UserPrincipal;
import com.bossware.jwtdemoapp.persistance.repositories.UserRepository;
import com.bossware.jwtdemoapp.persistance.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

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
}
