package com.grabbler.grabblerapi.repositories;

import com.grabbler.grabblerapi.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByMessageText(String messageText);
    Optional<Message> findById(Long id);
}
