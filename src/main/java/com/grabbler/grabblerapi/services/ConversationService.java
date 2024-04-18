package com.grabbler.grabblerapi.services;

import com.grabbler.grabblerapi.repositories.ConversationRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import com.grabbler.grabblerapi.model.Conversation;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;


    public Conversation getConversationById(Long id) {
        Optional<Conversation> conversation = conversationRepository.findById(id);

        if(conversation.isEmpty()) {
            throw new EntityNotFoundException("Conversation not found with id: " + id);
        }

        return conversation.get();
    }
}
