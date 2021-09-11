package com.appspring.service.impl;

import com.appspring.entity.Note;
import com.appspring.entity.User;
import com.appspring.entity.model.Role;
import com.appspring.exception.BadRequestException;
import com.appspring.exception.ForbiddenException;
import com.appspring.exception.PasswordNotValidException;
import com.appspring.mapper.NoteMapper;
import com.appspring.repository.NoteRepository;
import com.appspring.rest.dto.NoteRqDto;
import com.appspring.rest.dto.NoteRsDto;
import com.appspring.service.NoteService;
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
public class NoteServiceImpl implements NoteService {

    private static final String USER_ALREADY_EXISTS = "Пользователь с Логином = %s уже зарегистрирован";
    private static final String USER_NOT_FOUND = "Пользователь с id = %d не найден";
    private static final String PASSWORD_NOT_SET  = "Текущий пароль не задан!";
    private static final String PASSWORD_NOT_MATCH   = "Введённый пароль не совпадает с текущим";
    private static final String USER_OWN   = "Нельзя удалить собственную учетную запись";

    private final PasswordEncoder passwordEncoder;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<NoteRsDto> findAll(Specification<Note> filter, Pageable pageable) {
        return noteRepository.findAll(filter, pageable)
                .map(noteMapper::entityToRsDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<NoteRsDto> getById(Long id) {
        return noteRepository.findById(id)
                .filter(user -> user.getDeleted().equals(false))
                .map(noteMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public NoteRsDto create(NoteRqDto NoteRqDto) {
        validateUser(NoteRqDto);
        User user = noteMapper.rqDtoToEntity(NoteRqDto);
        user.setPassword(passwordEncoder.encode(NoteRqDto.getNewPassword()));
        Instant now = Instant.now();
        user.setCreated(now);
        user.setUpdated(now);

        return noteMapper.entityToRsDto(noteRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<NoteRsDto> getCurrentUser() {
        UserDetails userDetails = getCurrentUserDetails();
        return Objects.isNull(userDetails) ? Optional.empty() : findByLogin(userDetails.getUsername());
    }

    @Transactional
    @Override
    public Optional<NoteRsDto> findByLogin(String login) {
        return noteRepository.findByLogin(login)
                .filter(user -> user.getDeleted().equals(false))
                .map(noteMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public Optional<NoteRsDto> update(Long id, NoteRqDto NoteRqDto) {
        User user = noteRepository.findById(id)
                .filter(isDelete -> isDelete.getDeleted().equals(false))
                .orElseThrow(() -> {
                    log.warn("Пользователь для обновления с id = {} не найден.", id);
                    throw new BadRequestException(String.format(USER_NOT_FOUND, id));
                });

        validateUserUpdate(user, NoteRqDto);

        BeanUtils.copyProperties(NoteRqDto, user, BeanUtilsHelper.getNullPropertyNames(
                NoteRqDto, "password", "role"));

        if (Objects.nonNull(NoteRqDto.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(NoteRqDto.getNewPassword()));
        }

        Instant now = Instant.now();
        user.setUpdated(now);

        return Optional.of(user).map(noteMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (isCurrentUser(id)) {
            log.warn("Попытка удалить собственную запись");
            throw new ForbiddenException(USER_OWN);
        }

        noteRepository.findById(id)
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

    private void validateUserUpdate(User user, NoteRqDto NoteRqDto) {
        Boolean isAdmin = isAdmin();

        if (!isAdmin &&
                StringUtils.isEmpty(NoteRqDto.getCurrentPassword()) &&
                !StringUtils.isEmpty(NoteRqDto.getNewPassword())) {
            log.warn("При обновлении пароля пользователя с id = {} не задан текущий пароль.", user.getId());
            throw new BadRequestException(PASSWORD_NOT_SET);
        }

        if (!isAdmin &&
                !StringUtils.isEmpty(NoteRqDto.getCurrentPassword()) &&
                !StringUtils.isEmpty(NoteRqDto.getNewPassword()) &&
                !passwordEncoder.matches(NoteRqDto.getCurrentPassword(), user.getPassword())) {
            log.warn("При обновлении пароля пользователя с id = {} не верно введен пароль", user.getId());
            throw new PasswordNotValidException(PASSWORD_NOT_MATCH);
        }
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    public void validateUser(NoteRqDto NoteRqDto) {
        String login = NoteRqDto.getLogin();
        if (noteRepository.findByLogin(login).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(USER_ALREADY_EXISTS, login));
        }
    }

    private boolean isCurrentUser(Long id) {
        return getCurrentUser()
                .map(NoteRsDto::getId)
                .filter(currentUserId -> currentUserId.equals(id))
                .isPresent();
    }
}
