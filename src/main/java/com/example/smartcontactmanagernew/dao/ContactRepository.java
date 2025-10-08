package com.example.smartcontactmanagernew.dao;

import com.example.smartcontactmanagernew.entities.Contact;
import com.example.smartcontactmanagernew.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByUser(User user, Pageable pageable);
}

