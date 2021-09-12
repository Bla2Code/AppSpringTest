package com.appspring.service;

import com.appspring.entity.Note;
import com.appspring.rest.dto.NoteRqDto;
import com.appspring.rest.dto.NoteRsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;


public interface NoteService {

    /**
     * Запрос списка всех заметок
     *
     * @param filter   - для поиска заметок с фильтрацией
     * @param pageable {@link Pageable}
     * @return {@link Page} of {@link NoteRsDto}
     */
    Page<NoteRsDto> findAll(Specification<Note> filter, Pageable pageable);

    /**
     * Создание заметки
     *
     * @param NoteRq {@link NoteRqDto}
     * @return {@link Optional} of {@link NoteRsDto}
     */
    NoteRsDto create(NoteRqDto NoteRq);

    /**
     * Запрос заметки по ID
     *
     * @param id ID заметки
     * @return {@link Optional} of {@link NoteRsDto}
     */
    Optional<NoteRsDto> getById(Long id);

    /**
     * Обновление данных заметки по ID
     *
     * @param id        ID заметки
     * @param NoteRqDto данные для обновления
     * @return {@link Optional} of {@link NoteRsDto}
     */
    Optional<NoteRsDto> update(Long id, NoteRqDto NoteRqDto);

    /**
     * Удаление заметки по ID
     *
     * @param id ID заметки
     */
    void delete(Long id);

}
