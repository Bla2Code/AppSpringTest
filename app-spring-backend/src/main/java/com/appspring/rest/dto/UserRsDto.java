package com.appspring.rest.dto;

import com.appspring.entity.model.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRsDto {

    @ApiModelProperty(value = "ID пользователя", example = "1")
    private Long id;

    @ApiModelProperty(value = "Логин", example = "myLogin")
    private String login;

    @ApiModelProperty(value = "Роль", example = "ROLE_USER")
    private Role role;

    @ApiModelProperty(value = "Дата создания пользователя")
    private Instant created;

    @ApiModelProperty(value = "Дата последнего обновления")
    private Instant updated;
}
