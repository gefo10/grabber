package com.grabbler.grabblerapi.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
    

@Entity
@Table(name="conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="conversation_name")
    @Getter @Setter
    private String conversationName;

    @Column(name="is_group")
    @Getter @Setter
    private boolean is_group;
    
    @Column(name="created_at")
    @Getter @Setter
    private Timestamp created_at;

    @ManyToMany
    @JoinTable(
        name = "user_conversations",
        joinColumns = @JoinColumn(name = "conversation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    public Conversation() {
    }

    public Conversation(String conversation_name, boolean is_group, Timestamp created_at) {
        this.conversationName = conversation_name;
        this.is_group = is_group;
        this.created_at = created_at;
    }
}
