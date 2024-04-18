package com.grabbler.grabblerapi.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name="messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="content", nullable = false)
    private String content;

    public Message() {
    }

    public Message(Conversation conversation, String messageText) {
        this.conversation = conversation;
        this.content = messageText; 
    }


    public void setContent(String content) {
        this.content = content;
    }


}
