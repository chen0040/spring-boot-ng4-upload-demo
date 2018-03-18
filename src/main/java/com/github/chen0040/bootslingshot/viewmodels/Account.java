package com.github.chen0040.bootslingshot.viewmodels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String token;
    private String error;

    public Account() {}
    public Account(String username, String password, String token, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Account createAlert(String error) {
        return new Account().alert(error);
    }

    public Account alert(String error) {
        this.error = error;
        return this;
    }
}
