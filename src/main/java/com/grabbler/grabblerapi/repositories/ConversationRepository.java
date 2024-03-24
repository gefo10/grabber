package com.grabbler.grabblerapi.repositories;

import com.grabbler.grabblerapi.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByConversationName(String conversation_name);
    Optional<Conversation> findById(Long id);
}
