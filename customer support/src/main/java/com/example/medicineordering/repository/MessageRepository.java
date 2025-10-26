package com.example.medicineordering.repository;

import com.example.medicineordering.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByStatus(String status);
    List<Message> findByArchived(boolean archived); // New query for archived messages
}