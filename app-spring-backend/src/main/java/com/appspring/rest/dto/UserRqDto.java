package com.appspring.rest.dto;

import com.appspring.entity.model.Role;
import com.appspring.rest.dto.validation.CreateRq;
import com.appspring.rest.dto.validation.UpdateRq;
import com.appspring.security.ValidPassword;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRqDto {

    @ApiModelProperty(value = "Логин", example = "user1")
    @NotEmpty(groups = {CreateRq.class, UpdateRq.class})
    private String login;

    @ApiModelProperty(value = "Текущий пароль пользователя")
    @ToString.Exclude
    @ValidPassword(groups = {UpdateRq.class})
    private String currentPassword;

    @ApiModelProperty(value = "Новый пароль пользователя")
    @NotEmpty(groups = {CreateRq.class})
    @ToString.Exclude
    @ValidPassword(groups = {CreateRq.class, UpdateRq.class})
    private String newPassword;

    @ApiModelProperty(value = "Роль", example = "ROLE_ADMIN")
    @NotNull(groups = {CreateRq.class, UpdateRq.class})
    private Role role;

}