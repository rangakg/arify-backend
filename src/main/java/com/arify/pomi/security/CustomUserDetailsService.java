package com.arify.pomi.security;

import com.arify.pomi.entity.StaffUserEntity;
import com.arify.pomi.repository.StaffUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        StaffUserEntity user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User inactive");
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                List.of(() -> "ROLE_" + user.getRole()));
    }
}