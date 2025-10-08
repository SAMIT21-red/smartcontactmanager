package com.example.smartcontactmanagernew.controller;

import com.example.smartcontactmanagernew.dao.UserRepository;
import com.example.smartcontactmanagernew.entities.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SmartController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ Public Home Page
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact");
        return "home";
    }

    // ✅ Public About Page
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact");
        return "about";
    }

    // ✅ Show Signup Page
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("title", "Signup - Smart Contact");
        model.addAttribute("user", new User());
        return "signup";
    }



    // ✅ Handle Registration Form
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        System.out.println("📥 Incoming User: " + user);

        if (result.hasErrors()) {
            System.out.println("❌ Validation Errors: " + result.getAllErrors());
            model.addAttribute("user", user);
            return "signup";
        }

        if (!user.isAgreement()) {
            System.out.println("⚠️ Terms not accepted!");
            model.addAttribute("message", "You must accept the Terms & Conditions!");
            return "signup";
        }

        // Save user
        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setImageurl("default.png");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        System.out.println("✅ User Saved Successfully: " + user);

        model.addAttribute("message", "Signup successful!");
        model.addAttribute("user", new User()); // Reset form

        return "signup";
    }

    // ✅ Show Login Page
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Login - Smart Contact");
        return "login";
    }
}
