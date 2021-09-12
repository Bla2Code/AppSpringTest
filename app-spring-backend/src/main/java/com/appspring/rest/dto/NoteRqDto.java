package com.appspring.rest.dto;

import com.appspring.rest.dto.validation.CreateRq;
import com.appspring.rest.dto.validation.UpdateRq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRqDto {

    @ApiModelProperty(value = "Название заметки", example = "Очень полезная заметка")
    @NotEmpty(groups = {CreateRq.class, UpdateRq.class})
    private String name;

    @ApiModelProperty(value = "Текст заметки", example = "Класс Optional, при умелом использовании, значительно сокращает возможности приложения рухнуть с NullPoinerException, делая его более понятным и компактным, чем как если бы Вы делали бесчисленные проверки на null.")
    @NotEmpty(groups = {CreateRq.class, UpdateRq.class})
    private String description;

}
