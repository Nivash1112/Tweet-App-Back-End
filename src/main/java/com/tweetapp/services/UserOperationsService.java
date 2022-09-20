package com.tweetapp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tweetapp.exception.DetailsMisMatchException;
import com.tweetapp.exception.UserAlreadyExists;
import com.tweetapp.model.User;
import com.tweetapp.repositories.UserRepository;

import lombok.extern.log4j.Log4j2;



@Log4j2
@Component("user-model-service")
public class UserOperationsService {

	@Autowired
    private UserRepository userRepository;   

    public User findByUsername(final String username) {
        final User userModel = userRepository.findByUsername(username);
        return new User(userModel.getUsername(), userModel.getFirstName(),
                userModel.getLastName(), userModel.getEmail(), "null", "null");
    }

    public User createUser(final User user) throws UserAlreadyExists {
        final User foundedUser = userRepository.findByUsername(user.getUsername());
        if (foundedUser != null) {
            log.error("username already exists: {}", user.getUsername());
            throw new UserAlreadyExists("username already exists");
        }
        log.debug("registered user with userName: {}", user.getEmail());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        final List<User> userModels = userRepository.findAll();
        log.debug("total No of users: {}", userModels.size());
        return userModels.stream().map(user -> new User(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), "null",
                "null")).collect(Collectors.toList());
    }

    public User changePassword(final String username, final String newPassword, final String contact) throws DetailsMisMatchException {
        final User userDetails = userRepository.findByUsername(username);
        if (userDetails.getContactNum().equalsIgnoreCase(contact)
                && userDetails.getUsername().equalsIgnoreCase(username)) {
            userDetails.setPassword(newPassword);
            log.debug("password changed successfully for user: {}", username);
            return userRepository.save(userDetails);
        } else {
            log.error(String.format("unable to change password for user: %s", username));
            throw new DetailsMisMatchException(String.format("unable to change password for user: %s", username));
        }
    }

    public List<User> getUsersByUsername(final String username) {
        return userRepository.searchByUsername(username);
    }
    
}
