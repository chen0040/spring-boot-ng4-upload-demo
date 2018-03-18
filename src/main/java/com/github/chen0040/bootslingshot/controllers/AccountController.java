package com.github.chen0040.bootslingshot.controllers;

import com.github.chen0040.bootslingshot.services.AccountApi;
import com.github.chen0040.bootslingshot.viewmodels.Account;
import com.github.chen0040.bootslingshot.viewmodels.LoginObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountController {

    @Autowired
    private AccountApi service;

    @RequestMapping(value="/erp/login-api-json", method= RequestMethod.POST, consumes = "application/json")
    public @ResponseBody Account login(@RequestBody LoginObj loginObj) {
        return service.login(loginObj);
    }

    @RequestMapping(value="/erp/validate-api-json", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    TokenObj validateUser(@RequestBody TokenObj tokenObj) {
        return service.validateUser(tokenObj);
    }

    @RequestMapping(value="/erp/logout-api-json", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody TokenObj logout(@RequestBody TokenObj tokenObj) {
        return service.logout(tokenObj);
    }
}
