package com.example.smartcontactmanagernew.controller;

import com.example.smartcontactmanagernew.dao.UserRepository;
import com.example.smartcontactmanagernew.entities.User;
import com.example.smartcontactmanagernew.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.access.PathPatternRequestTransformer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.smartcontactmanagernew.payload.PasswordChangeForm; // ⬅️ New DTO import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // ⬅️ New import
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ✅ Dashboard page after login
    @GetMapping("/user/dashboard")
    public String dashboard(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("title", "User Dashboard");
        model.addAttribute("user", user);
        return "user/user-dashboard";
    }

    // Inside com.example.smartcontactmanagernew.controller.UserController

//    @GetMapping("/user/dashboard") // Or whatever maps to your home.html
//    public String dashboard(Model model, Principal principal) {
//        String userName = principal.getName();
//
//        User user = userRepository.findByEmail(userName)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        model.addAttribute("title", "Dashboard Home");
//        model.addAttribute("user", user); // ⬅️ Ensures the name displays correctly
//
//        // ⬅️ OPTIONAL: Add actual contact count here (requires injection of ContactRepository)
//        // model.addAttribute("totalContacts", contactRepository.countByUser(user));
//
//        return "home"; // Assuming your controller returns "home" to load home.html
//    }








    // ✅ Optional separate index page
    @GetMapping("/user/index")
    public String index(Model model, Principal principal) {
        String userName = principal.getName();

        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("title", "User Index");
        model.addAttribute("user", user);

        return "user/user-dashboard";
    }


    @GetMapping("/user/profile") // This handles the URL /user/profile
    public String yourProfile(Model model, Principal principal) {
        String userName = principal.getName();

        // Fetch the current logged-in user to display profile details
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("title", "Profile Page");
        model.addAttribute("user", user); // Pass the user object to the view

        // This returns the template located at src/main/resources/templates/user/profile.html
        return "user/profile";
    }


    // Inject the password encoder
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ... (your existing dashboard, profile methods)

    // 1. GET Mapping for Settings (Prepare the form)
    @GetMapping("/user/settings")
    public String openSettings(Model model, Principal principal) {
        String userName = principal.getName();

        // 1. Fetch the user (This is correct)
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Add the user object to the Model with the name "user" (The missing step!)
        model.addAttribute("user", user); // ⬅️ ADD THIS LINE

        // 3. Prepare the password form object
        model.addAttribute("passwordForm", new PasswordChangeForm());

        model.addAttribute("title", "Settings");

        return "user/settings";
    }

    // 2. POST Mapping to handle password change
    @PostMapping("/user/settings/change-password")
    public String changePassword(@ModelAttribute("passwordForm") PasswordChangeForm passwordForm,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 1: Check if the old password is correct
        if (!passwordEncoder.matches(passwordForm.getOldPassword(), user.getPassword())) {
            redirectAttributes.addFlashAttribute("message",
                    new Message("❌ Current password is incorrect!", "alert-danger"));
            return "redirect:/user/settings";
        }

        // Step 2: Check if new passwords match (You'll need a 'confirmNewPassword' in your DTO)
        // This is simplified; you'd typically handle this in the DTO or separate validation.

        // Step 3: Encode and Save the new password
        user.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message",
                new Message("✅ Password updated successfully!", "alert-success"));

        return "redirect:/user/settings";
    }

}
