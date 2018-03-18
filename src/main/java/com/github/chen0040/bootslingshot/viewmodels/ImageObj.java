package com.github.chen0040.bootslingshot.viewmodels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageObj {
    private long id;
    private long parentId;
    private String model;
    private String tag;
    private String token;
    private String error;

    public ImageObj alert(String error) {
        this.error = error;
        return this;
    }

    public static ImageObj createAlert(String error) {
        return new ImageObj().alert(error);
    }

}
