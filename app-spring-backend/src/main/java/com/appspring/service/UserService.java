package com.appspring.service;

import com.appspring.entity.User;
import com.appspring.rest.dto.UserRqDto;
import com.appspring.rest.dto.UserRsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface UserService {

    /**
     * Запрос списка всех пользователей системы
     *
     * @param filter   - для поиска пользователей с фильтрацией
     * @param pageable {@link Pageable}
     * @return {@link Page} of {@link UserRsDto}
     */
    Page<UserRsDto> findAll(Specification<User> filter, Pageable pageable);

    /**
     * Создание пользователя
     *
     * @param userRq {@link UserRqDto}
     * @return {@link Optional} of {@link UserRsDto}
     */
    UserRsDto create(UserRqDto userRq);

    /**
     * Запрос пользователя по ID
     *
     * @param id ID пользователя
     * @return {@link Optional} of {@link UserRsDto}
     */
    Optional<UserRsDto> getById(Long id);

    /**
     * Запрос текущего авторизованного пользователя
     *
     * @return {@link Optional} of {@link UserRsDto}
     */
    Optional<UserRsDto> getCurrentUser();

    /**
     * Запрос пользователя по логину
     *
     * @param login имя пользователя
     * @return {@link Optional} of {@link UserRsDto}
     */
    Optional<UserRsDto> findByLogin(String login);

    /**
     * Обновление данных пользователя по ID
     *
     * @param id        ID пользователя
     * @param userRqDto данные для обновления
     * @return {@link Optional} of {@link UserRsDto}
     */
    Optional<UserRsDto> update(Long id, UserRqDto userRqDto);

    /**
     * Удаление пользователя по ID
     *
     * @param id ID пользователя
     */
    void delete(Long id);

    /**
     * Проверить является ли id текущего пользователя
     * @param id пользователя
     * @return Boolean true если id принадлежит текущему пользователю
     */
    Boolean isCurrentUser(Long id);
}
