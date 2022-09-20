package com.tweetapp.exception;


public class UserAlreadyExists extends Exception {

    public UserAlreadyExists(String msg) {
        super(msg);
    }
}
