package com.lee.walletwise.service;

import com.lee.walletwise.dto.AuthRequest;
import com.lee.walletwise.dto.AuthResponse;
import com.lee.walletwise.dto.RegisterRequest;
import com.lee.walletwise.entity.Role;
import com.lee.walletwise.entity.User;
import com.lee.walletwise.entity.Wallet;
import com.lee.walletwise.repository.UserRepository;
import com.lee.walletwise.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // create wallet
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(java.math.BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);

        String jwt = jwtService.generateToken(user.getEmail());
        return new AuthResponse(jwt);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String jwt = jwtService.generateToken(request.email());
        return new AuthResponse(jwt);
    }
}