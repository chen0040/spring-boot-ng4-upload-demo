package com.github.chen0040.bootslingshot.viewmodels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryObj {
    private long id;
    private String tag;
    private String model;
    private String error;
    private String token;

    public BinaryObj alert(String error) {
        this.error = error;
        return this;
    }

    public static BinaryObj createAlert(String error){
        return new BinaryObj().alert(error);
    }
}
