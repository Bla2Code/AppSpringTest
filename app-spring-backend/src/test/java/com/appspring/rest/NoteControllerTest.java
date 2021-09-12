package com.appspring.rest;

import com.appspring.dataset.NoteDataset;
import com.appspring.dataset.UserDataset;
import com.appspring.entity.Note;
import com.appspring.repository.NoteRepository;
import com.appspring.rest.dto.NoteRqDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static com.appspring.util.MockMvcHelper.postJson;
import static com.appspring.util.MockMvcHelper.putJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserDataset userDataset;

    @Autowired
    private NoteDataset noteDataset;

    @BeforeEach
    void setUp() {
        userDataset.createData();
        noteDataset.createData();
    }

    @AfterEach
    void tearDown() {
        noteDataset.removeData();
        userDataset.removeData();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllAdminTest() throws Exception {
        mockMvc.perform(get("/notes"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.notNullValue()))
                .andExpect(jsonPath("content", Matchers.hasSize(3)))
                .andExpect(jsonPath("totalElements", Matchers.is(3)))
                .andExpect(jsonPath("totalPages", Matchers.is(1)))
                .andExpect(jsonPath("numberOfElements", Matchers.is(3)))
                .andExpect(jsonPath("empty", Matchers.is(false)));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void getAllUserTest() throws Exception {

        noteRepository.findAll();

        mockMvc.perform(get("/notes"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.notNullValue()))
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements", Matchers.is(1)))
                .andExpect(jsonPath("totalPages", Matchers.is(1)))
                .andExpect(jsonPath("numberOfElements", Matchers.is(1)))
                .andExpect(jsonPath("empty", Matchers.is(false)));
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void getByIdTest() throws Exception {
        Note note = noteRepository.findAll().stream()
                .filter(user2 -> "user2".equals(user2.getUser().getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Заметка пользователя user1 не найдена."));

        mockMvc.perform(get("/notes/" + note.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.equalTo(note.getId().intValue())))
                .andExpect(jsonPath("name", Matchers.notNullValue()))
                .andExpect(jsonPath("description", Matchers.notNullValue()));
    }

    @Test
    @WithMockUser(username = "user2", roles = "USER")
    void getByIdUserTest() throws Exception {
        Note note = noteRepository.findAll().stream()
                .filter(user2 -> "admin1".equals(user2.getUser().getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Заметка пользователя admin1 не найдена."));

        mockMvc.perform(get("/notes/" + note.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user2", roles = "USER")
    void createTest() throws Exception {
        NoteRqDto noteRq = NoteRqDto.builder()
                .name("Очень важная заметка пользователя user2")
                .description("Текст очень важной заметки пользователя user2")
                .build();

        mockMvc.perform(postJson(noteRq, "/notes"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", Matchers.notNullValue()))
                .andExpect(jsonPath("name", Matchers.is(noteRq.getName())))
                .andExpect(jsonPath("description", Matchers.is(noteRq.getDescription())))
                .andExpect(jsonPath("user.login", Matchers.is("user2")));
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void updateTest() throws Exception {
        Note note = noteRepository.findAll().stream()
                .filter(user2 -> "user2".equals(user2.getUser().getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Заметка пользователя user2 не найдена."));

        NoteRqDto noteRq = NoteRqDto.builder()
                .name("Администратор отредактировал заметку пользователя user2")
                .description("Текст очень важной заметки пользователя user2")
                .build();

        mockMvc.perform(putJson(noteRq, "/notes/" + note.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.notNullValue()))
                .andExpect(jsonPath("name", Matchers.is(noteRq.getName())))
                .andExpect(jsonPath("description", Matchers.is(noteRq.getDescription())))
                .andExpect(jsonPath("user.login", Matchers.is(note.getUser().getLogin())));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void updateAdminUserTest() throws Exception {
        Note note = noteRepository.findAll().stream()
                .filter(user2 -> "admin1".equals(user2.getUser().getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Заметка пользователя user2 не найдена."));

        NoteRqDto noteRq = NoteRqDto.builder()
                .name("Пользователь хочет изменить заметку Администратора")
                .description("Текст очень важной заметки пользователя admin1")
                .build();

        mockMvc.perform(putJson(noteRq, "/notes/" + note.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void deleteTest() throws Exception {
        Note note = noteRepository.findAll().stream()
                .filter(user2 -> "user1".equals(user2.getUser().getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Заметка пользователя user1 не найдена."));

        mockMvc.perform(delete("/notes/" + note.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isAccepted());

        noteRepository.findById(note.getId())
                .ifPresentOrElse(value -> assertTrue(value.getDeleted()), () -> {
                    throw new RuntimeException("Заметка не найден.");
                });
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void deleteAdminUserTest() throws Exception {
        Note note = noteRepository.findAll().stream()
                .filter(user2 -> "admin1".equals(user2.getUser().getUsername()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Заметка пользователя admin1 не найдена."));

        mockMvc.perform(delete("/notes/" + note.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        noteRepository.findById(note.getId())
                .ifPresentOrElse(value -> assertFalse(value.getDeleted()), () -> {
                    throw new RuntimeException("Заметка не найден.");
                });
    }

}