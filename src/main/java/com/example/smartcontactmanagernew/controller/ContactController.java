package com.example.smartcontactmanagernew.controller;

import com.example.smartcontactmanagernew.dao.ContactRepository;
import com.example.smartcontactmanagernew.dao.UserRepository;
import com.example.smartcontactmanagernew.entities.Contact;
import com.example.smartcontactmanagernew.entities.User;
import com.example.smartcontactmanagernew.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    // --- VIEW / REDIRECT MAPPINGS ---

    @GetMapping("")
    public String defaultContacts() {
        return "redirect:/user/contacts/view";
    }

    @GetMapping("/view")
    public String redirectToFirstPage() {
        return "redirect:/user/contacts/0";
    }

    @GetMapping("/{page}")
    public String viewContacts(@PathVariable("page") Integer page,
                               Model model,
                               Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, 5); // 5 contacts per page
        Page<Contact> contacts = contactRepository.findByUser(user, pageable);

        model.addAttribute("contacts", contacts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("title", "View Contacts"); // Add title

        return "user/contacts_view"; // Thymeleaf template
    }

    @GetMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Long cId,
                                    Model model,
                                    Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactRepository.findById(cId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // **AUTHORIZATION CHECK: ID != ID means UNAUTHORIZED**
        if (contact.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized access");
        }

        model.addAttribute("contact", contact);
        model.addAttribute("title", contact.getName());
        return "user/contact-details";
    }


    // --- ADD CONTACT ---

    @GetMapping("/add")
    public String showAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "user/add_contact";
    }
    @PostMapping("/add")
    public String processAddContact(@ModelAttribute("contact") Contact contact,
                                    @RequestParam("profileImage") MultipartFile file,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (file.isEmpty()) {
                contact.setImage("default.png");
            } else {
                String uploadDir = new File("uploads").getAbsolutePath();
                new File(uploadDir).mkdirs();
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Files.copy(file.getInputStream(), Paths.get(uploadDir, fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(fileName);
            }

            contact.setUser(user);
            contactRepository.save(contact);

            // ✅ Flash message for success
            redirectAttributes.addFlashAttribute("message",
                    new Message("✅ Contact added successfully!", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            // ✅ Flash message for failure
            redirectAttributes.addFlashAttribute("message",
                    new Message("❌ Something went wrong!", "alert-danger"));
        }

        return "redirect:/user/contacts/view";
    }


    // --- EDIT CONTACT ---

    @GetMapping("/edit/{cId}")
    public String showEditContactForm(@PathVariable("cId") Long cId,
                                      Model model,
                                      Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactRepository.findById(cId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // **AUTHORIZATION CHECK: ID != ID means UNAUTHORIZED**
        if (contact.getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized access to edit contact");
        }

        model.addAttribute("contact", contact);
        model.addAttribute("title", "Edit Contact");
        return "user/contact_edit";
    }

    @PostMapping("/update/{cId}")
    public String processUpdateContact(@PathVariable("cId") Long cId,
                                       @ModelAttribute("contact") Contact contact,
                                       @RequestParam("profileImage") MultipartFile file,
                                       Principal principal,
                                       RedirectAttributes redirectAttributes) {
        try {
            Contact oldContact = contactRepository.findById(cId)
                    .orElseThrow(() -> new RuntimeException("Contact not found for update"));
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // **AUTHORIZATION CHECK: ID != ID means UNAUTHORIZED**
            if (oldContact.getUser().getId() != currentUser.getId()) {
                throw new RuntimeException("Unauthorized access to update contact");
            }

            // Image handling (keeping old image if new one is empty)
            if (!file.isEmpty()) {
                // Delete old image if it's not the default one
                if (!"default.png".equals(oldContact.getImage())) {
                    String uploadDir = new File("uploads").getAbsolutePath();
                    File oldFile = new File(uploadDir, oldContact.getImage());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                // Upload new image... (code remains the same)
                String uploadDir = new File("uploads").getAbsolutePath();
                new File(uploadDir).mkdirs();
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Files.copy(file.getInputStream(), Paths.get(uploadDir, fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(fileName);
            } else {
                contact.setImage(oldContact.getImage());
            }

            // Set mandatory fields
            contact.setId(cId);
            contact.setUser(currentUser);

            contactRepository.save(contact);

            // FIX: Use Message object for success
            redirectAttributes.addFlashAttribute("message",
                    new Message("✅ Contact updated successfully!", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            // FIX: Use Message object for failure
            redirectAttributes.addFlashAttribute("message",
                    new Message("❌ Something went wrong while updating!", "alert-danger"));
        }
        return "redirect:/user/contacts/contact/" + cId;
    }


    // --- DELETE CONTACT ---

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Long cId,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactRepository.findById(cId).orElse(null);

        // **AUTHORIZATION CHECK: ID == ID means AUTHORIZED**
        if (contact != null && contact.getUser().getId() == currentUser.getId()) {

            // File deletion logic
            if (!"default.png".equals(contact.getImage())) {
                try {
                    String uploadDir = new File("uploads").getAbsolutePath();
                    File oldFile = new File(uploadDir, contact.getImage());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                } catch (Exception e) {
                    System.out.println("Error deleting file: " + e.getMessage());
                }
            }

            contactRepository.delete(contact);

            // FIX: Use Message object for success
            redirectAttributes.addFlashAttribute("message", "Contact deleted successfully!");
        } else {
            // FIX: Use Message object for failure/unauthorized access
            redirectAttributes.addFlashAttribute("message", "Something went wrong!");
        }

        return "redirect:/user/contacts/view";
    }


}

