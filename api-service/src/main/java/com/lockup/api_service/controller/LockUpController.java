package com.lockup.api_service.controller;

import com.lockup.api_service.entity.User;
import com.lockup.api_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class LockUpController {

    @Autowired private UserRepository repo;
    @Autowired private BCryptPasswordEncoder encoder;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword())); // Encrypted password requirement
        repo.save(user);
        return ResponseEntity.ok("Register API: User Created Successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody User req) {
        User user = repo.findByUsername(req.getUsername());
        if (user != null && encoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(Map.of("message", "Login API: Success", "token", "session-jwt-123"));
        }
        return ResponseEntity.status(401).body("Login API: Failed");
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(repo.findById(id).orElseThrow());
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> editProfile(@PathVariable Long id, @RequestBody User updates) {
        User user = repo.findById(id).orElseThrow();
        user.setEmail(updates.getEmail());
        repo.save(user);
        return ResponseEntity.ok("Edit Profile API: Success");
    }

    @PatchMapping("/profile/{id}/password")
    public ResponseEntity<?> editPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = repo.findById(id).orElseThrow();
        user.setPassword(encoder.encode(body.get("newPassword")));
        repo.save(user);
        return ResponseEntity.ok("Edit Password API: Success");
    }

    @PostMapping("/users/{id}/upload")
    public ResponseEntity<?> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        User user = repo.findById(id).orElseThrow();
        user.setProfilePicture(file.getBytes()); // Converted to BLOB/BYTES
        repo.save(user);
        return ResponseEntity.ok("Upload Photo API: " + file.getOriginalFilename() + " saved as BLOB");
    }
}