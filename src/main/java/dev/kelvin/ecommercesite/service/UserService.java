package dev.kelvin.ecommercesite.service;

import dev.kelvin.ecommercesite.dto.ChangePasswordRequest;
import dev.kelvin.ecommercesite.exception.ResourceNotFoundException;
import dev.kelvin.ecommercesite.model.User;
import dev.kelvin.ecommercesite.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailConfirmed(false);
        user.setEmailConfirmationCode(generateConfirmationCode());
        user.setRole(User.Role.ADMIN);
        emailService.sendConfirmationEmail(user);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Resource NOT FOUND")
        );
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getUserByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(),user.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void confirmEmail(String email, String confirmationCode) {
        User user = getUserByEmail(email);
        if (user.getEmailConfirmationCode().equals(confirmationCode)) {
            user.setEmailConfirmed(true);
            user.setEmailConfirmationCode(null);
        }else {
            throw new BadCredentialsException("Invalid confirmation code");
        }
    }

    private String generateConfirmationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
