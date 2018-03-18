package com.github.chen0040.bootslingshot.services;

import com.github.chen0040.bootslingshot.viewmodels.Account;
import com.github.chen0040.bootslingshot.viewmodels.LoginObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;
import com.github.chen0040.bootslingshot.viewmodels.UsernameAndToken;

public interface AccountApi {
    Account login(LoginObj loginObj);

    TokenObj validateUser(TokenObj tokenObj);

    TokenObj logout(TokenObj tokenObj);

    UsernameAndToken validateToken(String token);
}
