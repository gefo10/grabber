package com.grabbler.grabblerapi.controller;

import com.grabbler.grabblerapi.model.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.grabbler.grabblerapi.services.ConversationService;
import com.grabbler.grabblerapi.services.UserService;

@Controller
public class ChatController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

     

    @MessageMapping("/{conversationId}/sendMessage")
    @SendTo("/topic/messages/{conversationId}")
    public Message sendMessage(@DestinationVariable Long conversationId, Message message) {
        // Here you can optionally save the message to a database
        return message;
    }

    @MessageMapping("/{conversationId}/addUser/{userId}")
    @SendTo("/topic/messages/{conversationId}/")
    public Message addUser(@DestinationVariable Long conversationId, @DestinationVariable Long userId, Message message) {
         // Handle adding a user to the chat, e.g., update chat participant list in database

        message.setContent("User " + userId + " has joined the conversation");
        return message;
    }

}
