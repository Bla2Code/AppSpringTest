package com.appspring.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteFilterDto {

    @ApiModelProperty(value = "Фильтр имя заметки")
    private String name;

    @ApiModelProperty(value = "Фильтр текст заметки")
    private String description;

}
