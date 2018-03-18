package com.github.chen0040.bootslingshot.utils;

import com.github.chen0040.bootslingshot.viewmodels.UsernameAndToken;

public class Helpers {
    public static boolean isValidAccount(UsernameAndToken account) {
        return !StringUtils.isEmpty(account.getUsername());
    }
}
