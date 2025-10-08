package com.example.smartcontactmanagernew.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String password;

    @Column(unique = true)
    private String email;

    private String role;

    private String imageurl;

    private boolean enabled;

    // ✅ Not saved in DB, only for signup form
    @Transient
    private boolean agreement;

    // The field must exist
    private String about;

    // The public getter method is required by Spring
    public String getAbout() {
        return about;
    }

    // The public setter method is usually needed for data binding
    public void setAbout(String about) {
        this.about = about;
    }

    // One-to-many relation
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    // ====== Getters and Setters ======
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getImageurl() { return imageurl; }
    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isAgreement() { return agreement; }
    public void setAgreement(boolean agreement) { this.agreement = agreement; }

    public List<Contact> getContacts() { return contacts; }
    public void setContacts(List<Contact> contacts) { this.contacts = contacts; }

    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setUser(this);
    }
}
