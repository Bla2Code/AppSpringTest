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

import java.time.Instant;
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
                .filter(user -> user.getDeleted().equals(false))
                .map(userMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public UserRsDto create(UserRqDto userRqDto) {
        validateUser(userRqDto);
        User user = userMapper.rqDtoToEntity(userRqDto);
        user.setPassword(passwordEncoder.encode(userRqDto.getNewPassword()));
        Instant now = Instant.now();
        user.setCreated(now);
        user.setUpdated(now);

        return userMapper.entityToRsDto(userRepository.save(user));
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
                .filter(user -> user.getDeleted().equals(false))
                .map(userMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public Optional<UserRsDto> update(Long id, UserRqDto userRqDto) {
        User user = userRepository.findById(id)
                .filter(isDelete -> isDelete.getDeleted().equals(false))
                .orElseThrow(() -> {
                    log.warn("Пользователь для обновления с id = {} не найден.", id);
                    throw new BadRequestException(String.format(USER_NOT_FOUND, id));
                });

        validateUserUpdate(user, userRqDto);

        BeanUtils.copyProperties(userRqDto, user, BeanUtilsHelper.getNullPropertyNames(
                userRqDto, "password", "role"));

        if (Objects.nonNull(userRqDto.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(userRqDto.getNewPassword()));
        }

        Instant now = Instant.now();
        user.setUpdated(now);

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
                .filter(isDelete -> isDelete.getDeleted().equals(false))
                .ifPresentOrElse(
                        user -> {
                            user.setDeleted(true);
                            user.setUpdated(Instant.now());
                        },
                        () -> log.warn("Пользователь для удаления с id = {} не найден.", id));
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

        if (!isAdmin &&
                StringUtils.isEmpty(userRqDto.getCurrentPassword()) &&
                !StringUtils.isEmpty(userRqDto.getNewPassword())) {
            log.warn("При обновлении пароля пользователя с id = {} не задан текущий пароль.", user.getId());
            throw new BadRequestException(PASSWORD_NOT_SET);
        }

        if (!isAdmin &&
                !StringUtils.isEmpty(userRqDto.getCurrentPassword()) &&
                !StringUtils.isEmpty(userRqDto.getNewPassword()) &&
                !passwordEncoder.matches(userRqDto.getCurrentPassword(), user.getPassword())) {
            log.warn("При обновлении пароля пользователя с id = {} не верно введен пароль", user.getId());
            throw new PasswordNotValidException(PASSWORD_NOT_MATCH);
        }
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    public void validateUser(UserRqDto userRqDto) {
        String login = userRqDto.getLogin();
        if (userRepository.findByLogin(login).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(USER_ALREADY_EXISTS, login));
        }
    }

    private boolean isCurrentUser(Long id) {
        return getCurrentUser()
                .map(UserRsDto::getId)
                .filter(currentUserId -> currentUserId.equals(id))
                .isPresent();
    }
}
