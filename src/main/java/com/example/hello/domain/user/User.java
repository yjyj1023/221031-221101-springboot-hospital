package com.example.hello.domain.user;

import lombok.Setter;

@Setter
public class User {
    String id;
    String name;
    String password;

    //생성자 생성
    public User(String id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }

    //getter 생성
    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }


    public String getPassword() {
        return password;
    }

}