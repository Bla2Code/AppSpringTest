package com.appspring.rest;

import com.appspring.repository.specification.NoteSpecificationBuilder;
import com.appspring.rest.dto.NoteFilterDto;
import com.appspring.rest.dto.NoteRqDto;
import com.appspring.rest.dto.NoteRsDto;
import com.appspring.rest.dto.UserRsDto;
import com.appspring.rest.dto.validation.CreateRq;
import com.appspring.rest.dto.validation.UpdateRq;
import com.appspring.service.NoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.HttpURLConnection;

@Api(tags = "Заметки")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notes")
public class NoteController {

    private static final String NOTE_NOT_FOUND_BY_ID = "Заметка с id = %d не найдена";

    private final NoteService noteService;

    @ApiOperation("Запрос списка всех заметок пользователя")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @GetMapping
    public Page<NoteRsDto> getAll(@ModelAttribute NoteFilterDto filter, Pageable pageable) {
        return noteService.findAll(NoteSpecificationBuilder.getNoteConfigSpecification(filter), pageable);
    }

    @ApiOperation("Запрос заметки по ID")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @GetMapping("/{id}")
    public NoteRsDto getById(@PathVariable Long id) {
        return noteService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format(NOTE_NOT_FOUND_BY_ID, id)
                ));
    }

    @ApiOperation("Создание заметки")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Validated(CreateRq.class) NoteRqDto noteRq) {
        var note = noteService.create(noteRq);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(note.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(note);
    }


    @ApiOperation("Изменение заметки")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @PutMapping("/{id}")
    public NoteRsDto update(@PathVariable Long id, @RequestBody @Validated(UpdateRq.class) NoteRqDto note) {
        return noteService.update(id, note)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format(NOTE_NOT_FOUND_BY_ID, id)
                ));
    }

    @ApiOperation("Удаление заметки")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UserRsDto> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.accepted()
                .build();
    }

}
