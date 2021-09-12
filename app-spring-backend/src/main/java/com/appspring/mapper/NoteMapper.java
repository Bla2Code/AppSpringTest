package com.appspring.mapper;

import com.appspring.entity.Note;
import com.appspring.rest.dto.NoteRqDto;
import com.appspring.rest.dto.NoteRsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoteMapper {

    public NoteRsDto entityToRsDto(Note source) {
        log.debug("Note " + source.toString());
        return NoteRsDto.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .user(source.getUser())
                .created(source.getCreated())
                .updated(source.getUpdated())
                .build();
    }

    public Note rqDtoToEntity(NoteRqDto source) {
        log.debug("NoteRqDto " + source.toString());
        return Note.builder()
                .name(source.getName())
                .description(source.getDescription())
                .build();
    }
}
