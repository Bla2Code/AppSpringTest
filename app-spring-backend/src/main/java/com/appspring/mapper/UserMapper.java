package com.appspring.mapper;

import com.appspring.entity.User;
import com.appspring.entity.model.UserStatus;
import com.appspring.rest.dto.UserRqDto;
import com.appspring.rest.dto.UserRsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {

    public UserRsDto entityToRsDto(User source) {
        log.debug("User " + source.toString());
        return UserRsDto.builder()
                .id(source.getId())
                .login(source.getLogin())
                .role(source.getRole())
                .created(source.getCreated())
                .updated(source.getUpdated())
                .build();
    }

    public User rqDtoToEntity(UserRqDto source) {
        log.debug("userRqDto " + source.toString());
        return User.builder()
                .login(source.getLogin())
                .role(source.getRole())
                .status(UserStatus.ACTIVE) //Сейчас это фиктивный статус, ни как не обрабатывается
                .build();
    }
}
