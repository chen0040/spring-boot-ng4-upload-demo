package com.github.chen0040.bootslingshot.services;

import com.github.chen0040.bootslingshot.utils.StringUtils;
import com.github.chen0040.bootslingshot.viewmodels.Account;
import com.github.chen0040.bootslingshot.viewmodels.LoginObj;
import com.github.chen0040.bootslingshot.viewmodels.TokenObj;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;

@Service
public class AccountApiImpl implements AccountApi {

    private static final Logger logger = LoggerFactory.getLogger(AccountApiImpl.class);

    private Set<Account> database = new HashSet<>();
    private Map<String, Account> sessions = new HashMap<>();


    @Autowired
    private WebSocketService webSocketService;

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    public AccountApiImpl(){
        database.add(new Account("admin", "admin", "admin-123", "xs0040@gmail.com", "Admin", "Admin"));
        database.add(new Account("user", "user", "user-123", "xs0040@gmail.com", "Xianshun", "Chen"));
    }

    @Override
    public Account login(LoginObj loginObj) {
        final String username = loginObj.getUsername();
        final String password = loginObj.getPassword();
        for(Account account : database) {
            if(account.getUsername().equalsIgnoreCase(username) && account.getPassword().equals(password)) {
                sessions.put(account.getToken(), account);
                executor.submit(() -> {
                    try {
                        Thread.sleep(3000L);
                        webSocketService.send(account.getToken(), "validated", "Welcome, " + username);
                    }catch(Exception ex) {
                        logger.error("Failed to send web socket validated message", ex);
                    }
                });

                return account;
            }
        }
        return Account.createAlert("Invalid login");
    }

    @Override
    public TokenObj validateUser(TokenObj tokenObj) {
        logger.info("validating user: {}", tokenObj.getToken());
        final String token = tokenObj.getToken();
        tokenObj.setUsername("");
        for(String sessionToken : sessions.keySet()) {
            if(sessionToken.equals(token)) {
                Account account = sessions.get(token);
                String username = account.getUsername();
                tokenObj.setUsername(username);

                executor.submit(() -> {
                    try {
                        Thread.sleep(3000L);
                        logger.info("send websocket message on validated successfully ...");
                        webSocketService.send(account.getToken(), "validated", "Welcome, " + username);
                    }catch(Exception ex) {
                        logger.error("Failed to send web socket validated message", ex);
                    }
                });
                break;
            }
        }
        return tokenObj;
    }

    @Override
    public TokenObj logout(TokenObj tokenObj) {
        final String token = tokenObj.getToken();

        for(String sessionToken : sessions.keySet()) {
            if(sessionToken.equals(token)) {
                sessions.remove(sessionToken);
                tokenObj.setUsername("");
                break;
            }
        }
        return tokenObj;
    }
}
