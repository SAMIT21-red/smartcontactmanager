package com.example.smartcontactmanagernew.payload; // Or wherever you put models

public class PasswordChangeForm {
    private String oldPassword;
    private String newPassword;

    // Getters and Setters (Omitted for brevity, but required)

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "PasswordChangeForm{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }

    public PasswordChangeForm(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public PasswordChangeForm() {
        // Required for Spring/Thymeleaf to initialize the form object
    }

    // ... constructors, toString()
}