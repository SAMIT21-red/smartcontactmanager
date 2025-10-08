package com.example.smartcontactmanagernew.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cId;

    private String name;

    private String secondName;

    private String work;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(length = 5000)
    private String description;

    private String image; // profile picture for contact

    // Many contacts can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ Constructors
    public Contact() {}

    public Contact(String name, String secondName, String work, String email, String phone, String description, String image, User user) {
        this.name = name;
        this.secondName = secondName;
        this.work = work;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.image = image;
        this.user = user;
    }

    // ✅ Getters and Setters
    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ✅ equals & hashCode (good practice for entities)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(cId, contact.cId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cId);
    }

    // ✅ toString
    @Override
    public String toString() {
        return null;
    }

    public void setId(Long cId) {
    }
}
