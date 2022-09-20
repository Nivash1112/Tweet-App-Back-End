package com.tweetapp.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.tweetapp.repositories.UserRepository;


@Component("user-service")
public class MyUserDetailService implements UserDetailsService {


    private final UserRepository userRepository;
   

    public MyUserDetailService(@Qualifier("user-repository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        com.tweetapp.model.User foundedUser = userRepository.findByUsername(username);
        if (foundedUser == null) return null;
        final String name = foundedUser.getUsername();
        final String password = foundedUser.getPassword();
        return new User(name, password, new ArrayList<>());
    }
    
    

}
