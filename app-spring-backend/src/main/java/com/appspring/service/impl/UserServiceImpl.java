package com.appspring.service.impl;

import com.appspring.entity.User;
import com.appspring.entity.model.Role;
import com.appspring.exception.BadRequestException;
import com.appspring.exception.ForbiddenException;
import com.appspring.exception.PasswordNotValidException;
import com.appspring.mapper.UserMapper;
import com.appspring.repository.UserRepository;
import com.appspring.rest.dto.UserRqDto;
import com.appspring.rest.dto.UserRsDto;
import com.appspring.service.UserService;
import com.appspring.util.BeanUtilsHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_ALREADY_EXISTS = "Пользователь с Логином = %s уже зарегистрирован";
    private static final String USER_NOT_FOUND = "Пользователь с id = %d не найден";
    private static final String PASSWORD_NOT_SET  = "Текущий пароль не задан!";
    private static final String PASSWORD_NOT_MATCH   = "Введённый пароль не совпадает с текущим";
    private static final String USER_OWN   = "Нельзя удалить собственную учетную запись";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<UserRsDto> findAll(Specification<User> filter, Pageable pageable) {
        return userRepository.findAll(filter, pageable)
                .map(userMapper::entityToRsDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserRsDto> getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public UserRsDto create(UserRqDto userRqDto) {
        validateUser(userRqDto);
        User user = userMapper.rqDtoToEntity(userRqDto);
        user.setPassword(passwordEncoder.encode(userRqDto.getNewPassword()));
        User saved = userRepository.save(user);

        return userMapper.entityToRsDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserRsDto> getCurrentUser() {
        UserDetails userDetails = getCurrentUserDetails();
        return Objects.isNull(userDetails) ? Optional.empty() : findByLogin(userDetails.getUsername());
    }

    @Transactional
    @Override
    public Optional<UserRsDto> findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::entityToRsDto);
    }

    private Boolean isAdmin() {
        UserDetails currentUserDetails = getCurrentUserDetails();

        if(Objects.isNull(currentUserDetails)) {
            return false;
        }

        return currentUserDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.ROLE_ADMIN.name()));
    }

    private void validateUserUpdate(User user, UserRqDto userRqDto) {
        Boolean isAdmin = isAdmin();

        if (!isAdmin && StringUtils.isEmpty(userRqDto.getCurrentPassword())) {
            log.warn("При обновлении пользователя с id = {} не задан пароль.", user.getId());
            throw new BadRequestException(PASSWORD_NOT_SET);
        }

        if (!isAdmin && !passwordEncoder.matches(userRqDto.getCurrentPassword(), user.getPassword())) {
            log.warn("При обновлении пользователя с id = {} не верно введен пароль", user.getId());
            throw new PasswordNotValidException(PASSWORD_NOT_MATCH);
        }
    }

    @Transactional
    @Override
    public Optional<UserRsDto> update(Long id, UserRqDto userRqDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь для обновления с id = {} не найден.", id);
                    throw new BadRequestException(String.format(USER_NOT_FOUND, id));
                });

        validateUserUpdate(user, userRqDto);

        BeanUtils.copyProperties(userRqDto, user, BeanUtilsHelper.getNullPropertyNames(userRqDto, "password"));
        if (Objects.nonNull(userRqDto.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(userRqDto.getNewPassword()));
        }

        return Optional.of(user).map(userMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (isCurrentUser(id)) {
            log.warn("Попытка удалить собственную запись");
            throw new ForbiddenException(USER_OWN);
        }

        userRepository.findById(id)
                .ifPresentOrElse(
                        userRepository::delete,
                        () -> log.warn("Пользователь для удаления с id = {} не найден.", id));
    }

//    @Transactional
//    @Override
//    public Optional<UserRsDto> updateLastActivity(Instant activity, Long id) {
//        var user = userRepository.findById(id);
//        user.ifPresent(it -> it.setLastActivity(activity));
//        return user.map(userMapper::entityToRsDto);
//    }
//
//    @Transactional
//    @Override
//    public Optional<UserRsDto> block(Long id) {
//        if (isCurrentUser(id)) {
//            throw new BadRequestException("Нельзя заблокировать собственную учетную запись");
//        }
//        Optional<User> user = userRepository.findById(id);
//        user.ifPresent(it -> it.setStatus(UserStatus.BLOCKED));
//        return user.map(userMapper::entityToRsDto);
//    }
//
//    @Transactional
//    @Override
//    public Optional<UserRsDto> unblock(Long id) {
//        Optional<User> user = userRepository.findById(id);
//        user.ifPresent(it -> it.setStatus(UserStatus.ACTIVE));
//        return user.map(userMapper::entityToRsDto);
//    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    public void validateUser(UserRqDto userRqDto) {
        String login = userRqDto.getLogin();
        userRepository.findByLogin(login)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(USER_ALREADY_EXISTS, login))
                );
    }

    private boolean isCurrentUser(Long id) {
        return getCurrentUser()
                .map(UserRsDto::getId)
                .filter(currentUserId -> currentUserId.equals(id))
                .isPresent();
    }
}
