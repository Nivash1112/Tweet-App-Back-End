package com.tweetapp.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.dto.LoginResponse;
import com.tweetapp.dto.ForgotPassword;
import com.tweetapp.services.UserOperationsService;

@Log4j2
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserActivityController {

    private final UserOperationsService userModelService;

    public UserActivityController(@Qualifier("user-model-service") UserOperationsService userModelService) {
        this.userModelService = userModelService;
    }

    @PutMapping(value = "/tweets/{username}/forgot")
    public ResponseEntity<?> changePassword(@PathVariable("username") String username,
                                            @RequestBody ForgotPassword newPassword) {
        try {
            log.debug("changing password for user: {}", username);
            return new ResponseEntity<>(userModelService.changePassword(username, newPassword.getNewPassword(), newPassword.getContact()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new LoginResponse("Unable to change password"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/tweets/users/all")
    public ResponseEntity<?> getAllUsers() {
        log.debug("fetching total users");
        return new ResponseEntity<>(userModelService.getAllUsers(), HttpStatus.OK);

    }

    //method to search for like users by username
    @GetMapping(value = "/tweets/user/search/{username}")
    public ResponseEntity<?> searchForUsers(@PathVariable String username) {
        log.debug("fetching user by userName: {}", username);
        return new ResponseEntity<>(userModelService.getUsersByUsername(username), HttpStatus.OK);
    }
}
