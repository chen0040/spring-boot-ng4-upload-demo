package com.github.chen0040.bootslingshot.services;


/**
 * Created by xschen on 10/11/2017.
 */
public interface WebSocketService {
   void send(String vendor, String eventType, Object state);
}
