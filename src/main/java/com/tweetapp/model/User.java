package com.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "users_Db")
public class User {

    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNum;
    private String password;

}
