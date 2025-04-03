package com.bangkoo.back.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user")
public class User {

    @Id
    private String email;
    private String nickname;

    public User(){} //기본 생성자

     public User(String email, String nickname){
        this.email = email;
        this.nickname =nickname;
     }


}
