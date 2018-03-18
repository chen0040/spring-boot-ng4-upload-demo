package com.github.chen0040.bootslingshot.viewmodels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketEvent {
    private String eventType = "progress";
    private Object state;
}
