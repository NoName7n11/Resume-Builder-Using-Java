package com.resumebuilder.service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resumebuilder.model.User;
import com.resumebuilder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for user management operations
 * 
 * Note: Some methods have @SuppressWarnings("null") because Spring Data JPA's save()
 * method is declared with @NonNull return type, but Eclipse JDT's null-analysis
 * doesn't always recognize this guarantee.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public User createUser(@NonNull String email, @NonNull String password, 
                          @NonNull String firstName, @NonNull String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .provider(User.AccountProvider.LOCAL)
                .enabled(true)
                .roles(Set.of(User.Role.USER))
                .build();

        return userRepository.save(user);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public User createOAuthUser(@NonNull String email, @NonNull String firstName, 
                               @NonNull String lastName, @NonNull User.AccountProvider provider, 
                               @NonNull String providerId) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode("")) // OAuth users don't have passwords
                .firstName(firstName)
                .lastName(lastName)
                .provider(provider)
                .providerId(providerId)
                .enabled(true)
                .roles(Set.of(User.Role.USER))
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(@NonNull Long id) {
        return userRepository.findById(Objects.requireNonNull(id, "User ID must not be null"));
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public User updateUser(@NonNull Long userId, @NonNull String firstName, @NonNull String lastName) {
        User user = userRepository.findById(Objects.requireNonNull(userId, "User ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        return userRepository.save(user);
    }

    public void changePassword(@NonNull Long userId, @NonNull String oldPassword, @NonNull String newPassword) {
        User user = userRepository.findById(Objects.requireNonNull(userId, "User ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(@NonNull Long userId) {
        userRepository.deleteById(Objects.requireNonNull(userId, "User ID must not be null"));
    }
}
