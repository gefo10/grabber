package com.grabbler.grabblerapi.services;

import com.grabbler.grabblerapi.repositories.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import com.grabbler.grabblerapi.model.Conversation;

public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;


    public Conversation getConversationById(Long id) {
        Optional<Conversation> conversation = conversationRepository.findById(id);

        if (conversation.isPresent()) {
            return conversation.get();
        } else {
            throw new EntityNotFoundException("Conversation not found with id: " + id);
        }
    }

}
