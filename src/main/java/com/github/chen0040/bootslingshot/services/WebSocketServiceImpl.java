package com.github.chen0040.bootslingshot.services;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.chen0040.bootslingshot.viewmodels.WebSocketEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


/**
 * Created by xschen on 10/11/2017.
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

   @Autowired
   private SimpMessagingTemplate brokerMessagingTemplate;

   public void send(String vendor, String eventType, Object state) {
      WebSocketEvent event = new WebSocketEvent();
      event.setState(state);
      event.setEventType(eventType);
      brokerMessagingTemplate.convertAndSend("/topics/" + vendor + "/event", JSON.toJSONString(event, SerializerFeature.BrowserCompatible));
   }
}
