package com.tweetapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.model.User;


@Repository("user-repository")
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

    @Query("{'username':{'$regex':'?0','$options':'i'}}")
    List<User> searchByUsername(String username);
}
