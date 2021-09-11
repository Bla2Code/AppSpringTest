package com.appspring.rest;

import com.appspring.repository.specification.UserSpecificationBuilder;
import com.appspring.rest.dto.UserFilterDto;
import com.appspring.rest.dto.UserRqDto;
import com.appspring.rest.dto.UserRsDto;
import com.appspring.rest.dto.validation.CreateRq;
import com.appspring.rest.dto.validation.UpdateRq;
import com.appspring.service.UserService;
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

@Api(tags = "Пользователи")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private static final String USER_NOT_FOUND_BY_ID = "Пользователь с id = %d не найден";

    private final UserService userService;

    @ApiOperation("Запрос списка пользователей")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @GetMapping
    public Page<UserRsDto> getAll(@ModelAttribute UserFilterDto filter, Pageable pageable) {
        return userService.findAll(UserSpecificationBuilder.getEventConfigSpecification(filter), pageable);
    }

    @ApiOperation("Запрос пользователя по ID")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @GetMapping("/{id}")
    public UserRsDto getById(@PathVariable Long id) {
        return userService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format(USER_NOT_FOUND_BY_ID, id)
                ));
    }

    @ApiOperation("Запрос текущего авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @GetMapping("/current")
    public UserRsDto getCurrentUser() {
        return userService.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ApiOperation("Создание пользователя")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Validated(CreateRq.class) UserRqDto userRq) {
        var user = userService.create(userRq);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(user);
    }


    @ApiOperation("Обновление пользователя")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @PutMapping("/{id}")
    public UserRsDto update(@PathVariable Long id, @RequestBody @Validated(UpdateRq.class) UserRqDto user) {
        return userService.update(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format(USER_NOT_FOUND_BY_ID, id)
                ));
    }

    @ApiOperation("Удаление пользователя")
    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "OK"),
            @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "NOT FOUND"),
            @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "UNAUTHORIZED"),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "FORBIDDEN")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UserRsDto> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.accepted()
                .build();
    }

}
