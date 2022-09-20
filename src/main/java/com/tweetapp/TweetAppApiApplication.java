package com.tweetapp;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;


@Log4j2
@SpringBootApplication
public class TweetAppApiApplication {

    public static void main(String[] args) {
        log.info("Setting TimeZone to default..");
        TimeZone.setDefault(TimeZone.getDefault());
        SpringApplication.run(TweetAppApiApplication.class, args);
    }


}
