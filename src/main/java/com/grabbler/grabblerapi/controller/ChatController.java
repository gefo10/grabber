package com.grabbler.grabblerapi.controllers;

import com.grabbler.grabblerapi.model.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/{conversationId}/sendMessage")
    @SendTo("/topic/{conversationId}")
    public Message sendMessage(@DestinationVariable Long conversationId, Message message) {
        return message;
    }

    @MessageMapping("/{conversationId}/addUser")
    @SendTo("/topic/{conversationId}")
    public Message addUser(@DestinationVariable Long conversationId, Message message) {
        return message;
    }

}
