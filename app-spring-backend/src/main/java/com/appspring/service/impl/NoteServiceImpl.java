package com.appspring.service.impl;

import com.appspring.entity.Note;
import com.appspring.entity.User;
import com.appspring.entity.model.Role;
import com.appspring.exception.BadRequestException;
import com.appspring.mapper.NoteMapper;
import com.appspring.repository.NoteRepository;
import com.appspring.repository.UserRepository;
import com.appspring.rest.dto.NoteRqDto;
import com.appspring.rest.dto.NoteRsDto;
import com.appspring.service.NoteService;
import com.appspring.service.UserService;
import com.appspring.util.BeanUtilsHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private static final String NOTE_NOT_FOUND = "Заметка с id = %d не найден";

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<NoteRsDto> findAll(Specification<Note> filter, Pageable pageable) {
        List<NoteRsDto> noteRsDtoList = noteRepository.findAll(filter, pageable).stream()
                .filter(this::checkOwner)
                .map(noteMapper::entityToRsDto)
                .collect(Collectors.toList());

        return new PageImpl<>(noteRsDtoList, pageable, noteRsDtoList.size());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<NoteRsDto> getById(Long id) {
        return noteRepository.findById(id)
                .filter(this::checkOwner)
                .filter(note -> {
                    if (Objects.nonNull(note.getDeleted())) {
                        return note.getDeleted().equals(false);
                    }
                    return false;
                })
                .map(noteMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public NoteRsDto create(NoteRqDto NoteRqDto) {
        Note note = noteMapper.rqDtoToEntity(NoteRqDto);
        Instant now = Instant.now();
        note.setCreated(now);
        note.setUpdated(now);
        note.setUser(getNoteUser());

        return noteMapper.entityToRsDto(noteRepository.save(note));
    }

    private User getNoteUser() {
        return userService.getCurrentUser()
                .map(userRsDto -> userRepository.findById(userRsDto.getId()))
                .flatMap(user -> user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    @Override
    public Optional<NoteRsDto> update(Long id, NoteRqDto NoteRqDto) {
        Note note = noteRepository.findById(id)
                .filter(isDelete -> isDelete.getDeleted().equals(false))
                .filter(this::checkOwner)
                .orElseThrow(() -> {
                    log.warn("Заметка для обновления с id = {} не найден.", id);
                    throw new BadRequestException(String.format(NOTE_NOT_FOUND, id));
                });

        BeanUtils.copyProperties(NoteRqDto, note, BeanUtilsHelper.getNullPropertyNames(NoteRqDto));

        note.setUpdated(Instant.now());

        return Optional.of(note).map(noteMapper::entityToRsDto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        noteRepository.findById(id)
                .filter(isDelete -> isDelete.getDeleted().equals(false))
                .filter(this::checkOwner)
                .ifPresentOrElse(
                        note -> {
                            note.setDeleted(true);
                            note.setUpdated(Instant.now());
                        },
                        () -> {
                            log.warn("Запись для удаления с id = {} не найдена.", id);
                            throw new BadRequestException(String.format(NOTE_NOT_FOUND, id));
                        });
    }

    private Boolean checkOwner(Note note) {
        if (isAdmin()) {
            return true;
        }
        if (Objects.nonNull(note.getUser())) {
            return userService.isCurrentUser(note.getUser().getId());
        }
        return false;
    }

    private Boolean isAdmin() {
        UserDetails currentUserDetails = getCurrentUserDetails();

        if (Objects.isNull(currentUserDetails)) {
            return false;
        }

        return currentUserDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.ROLE_ADMIN.name()));
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

}
