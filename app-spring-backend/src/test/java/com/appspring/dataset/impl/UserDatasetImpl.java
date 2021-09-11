package com.appspring.dataset.impl;

import com.appspring.dataset.UserDataset;
import com.appspring.entity.User;
import com.appspring.entity.model.Role;
import com.appspring.entity.model.UserStatus;
import com.appspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDatasetImpl implements UserDataset {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createData() {
        Instant now1 = Instant.now();
        User user1 = User.builder()
                .login("admin1")
                .role(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode("111111"))
                .status(UserStatus.ACTIVE)
                .created(now1)
                .updated(now1)
                .build();

        Instant now2 = Instant.now();
        User user2 = User.builder()
                .login("admin2")
                .role(Role.ROLE_ADMIN)
                .password(passwordEncoder.encode("111111"))
                .status(UserStatus.ACTIVE)
                .created(now2)
                .updated(now2)
                .build();

        Instant now3 = Instant.now();
        User user3 = User.builder()
                .login("user1")
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode("111111"))
                .status(UserStatus.ACTIVE)
                .created(now3)
                .updated(now3)
                .build();

        Instant now4 = Instant.now();
        User user4 = User.builder()
                .login("user2")
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode("111111"))
                .status(UserStatus.ACTIVE)
                .created(now4)
                .updated(now4)
                .build();

        userRepository.saveAll(List.of(user1, user2, user3, user4));
    }

    @Override
    @Transactional
    public void removeData() {
        userRepository.deleteAllInBatch();
    }

}
