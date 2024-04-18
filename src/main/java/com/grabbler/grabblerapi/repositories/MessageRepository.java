package com.grabbler.grabblerapi.repositories;

import com.grabbler.grabblerapi.model.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByContent(String messageText);
    Optional<Message> findById(Long id);
}
