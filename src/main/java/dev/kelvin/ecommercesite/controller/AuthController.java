package dev.kelvin.ecommercesite.controller;

import dev.kelvin.ecommercesite.dto.ChangePasswordRequest;
import dev.kelvin.ecommercesite.dto.EmailConfirmationRequest;
import dev.kelvin.ecommercesite.dto.LogInRequest;
import dev.kelvin.ecommercesite.exception.ResourceNotFoundException;
import dev.kelvin.ecommercesite.model.User;
import dev.kelvin.ecommercesite.service.JwtService;
import dev.kelvin.ecommercesite.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LogInRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        final UserDetails userDetails = userService.getUserByEmail(request.getEmail());
        final String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user){
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.changePassword(email,request);
        return ResponseEntity.ok().body("Password changed");

    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestBody EmailConfirmationRequest request){
        try {
            userService.confirmEmail(request.getEmail(), request.getConfirmationCode());
            return ResponseEntity.ok().body("Email confirmed");
        }catch (BadCredentialsException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
