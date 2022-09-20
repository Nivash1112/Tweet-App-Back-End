package com.tweetapp.controllers;

import com.tweetapp.dto.LoginRequest;
import com.tweetapp.dto.LoginResponse;
import com.tweetapp.exception.UserAlreadyExists;
import com.tweetapp.model.User;
import com.tweetapp.services.UserOperationsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Api
@Log4j2
public class AuthorizationController {

	@Autowired
    private UserOperationsService userModelService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/tweets/register")
    public ResponseEntity<?> subscribeClient(@RequestBody User userModel) {

        try {
            User savedUser = userModelService.createUser(userModel);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (UserAlreadyExists e) {
            return new ResponseEntity<>(new LoginResponse("Given userId/email already exists"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new LoginResponse("Application facing some issue...!"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/tweets/login")
    public ResponseEntity<?> authenticateClient(@RequestBody LoginRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            return new ResponseEntity<>(new LoginResponse("Bad Credentials " + username), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userModelService.findByUsername(username), HttpStatus.OK);
    }
}
