package ro.spykids.server.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import ro.spykids.server.jwt.JwtService;
import ro.spykids.server.services.EncryptionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spykids/encryption")
@RequiredArgsConstructor
public class EncryptionController {
    private final JwtService jwtService;
    private final EncryptionService encryptionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> demo(@RequestParam String email, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            String tokenEmail = jwtService.extractEmail(token);
            if (email.equals(tokenEmail)) {
                return new ResponseEntity<>("Demo is working!", new HttpHeaders(), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Demo is UNAUTHORIZED!", new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/encrypt")
    @PreAuthorize("hasRole('ADMIN')")
    public String encrypt(@RequestParam String encrypt){
        return encryptionService.encrypt(encrypt);
    }

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String decrypt){
        return encryptionService.decrypt(decrypt);
    }
}