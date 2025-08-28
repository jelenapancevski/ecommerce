package com.bosch.miniecommerce.controllers;

import com.bosch.miniecommerce.dto.LoginRequestObject;
import com.bosch.miniecommerce.entities.User;
import com.bosch.miniecommerce.entities.UserType;
import com.bosch.miniecommerce.exceptions.ConflictException;
import com.bosch.miniecommerce.exceptions.UnauthorizedException;
import com.bosch.miniecommerce.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class AuthenticationController {
    public AuthenticationController(JwtEncoder jwtEncoder, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private JwtEncoder jwtEncoder;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private static final String SECRET_KEY = "414a6492282a5ce6b6cc528c88f20c9d74b5bdd77c84158e1febce10fe1f9143";
    private static final long EXPIRATION = 180000;
	// Register new user
    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@RequestBody User user){
            if (userRepository.findByUsername(user.getUsername()).isPresent()) throw new ConflictException("Username is taken.");
            if (userRepository.findByEmail(user.getEmail()).isPresent()) throw new ConflictException("Email is taken.");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setType(UserType.USER);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful!");
    }

    // Authenticate and issue JWT
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestObject request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new UnauthorizedException("Invalid credentials."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
           throw new UnauthorizedException("Invalid credentials.");
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .claim("id", user.getId())
                .claim("role", user.getType())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION))
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();

        return ResponseEntity.ok(Map.of("token", token));

    }


}
