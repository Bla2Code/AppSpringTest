package com.appspring.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @ApiModelProperty(value = "Логин", example = "admin")
    @NotEmpty
    private String login;

    @ApiModelProperty(value = "Пароль", example = "admin")
    @NotEmpty
    @ToString.Exclude
    private String password;
}
