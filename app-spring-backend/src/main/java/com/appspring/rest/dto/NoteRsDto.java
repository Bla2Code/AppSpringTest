package com.appspring.rest.dto;

import com.appspring.entity.User;
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
public class NoteRsDto {

    @ApiModelProperty(value = "ID заметки", example = "1")
    private Long id;

    @ApiModelProperty(value = "Название заметки", example = "Очень полезная заметка")
    private String name;

    @ApiModelProperty(value = "Текст заметки", example = "Класс Optional, при умелом использовании, значительно сокращает возможности приложения рухнуть с NullPoinerException, делая его более понятным и компактным, чем как если бы Вы делали бесчисленные проверки на null.")
    private String description;

    @ApiModelProperty(value = "Автор")
    private User user;

    @ApiModelProperty(value = "Дата создания заметки")
    private Instant created;

    @ApiModelProperty(value = "Дата последнего заметки")
    private Instant updated;

}
