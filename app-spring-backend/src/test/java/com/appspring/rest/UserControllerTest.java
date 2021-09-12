package com.appspring.rest;

import com.appspring.ContainerisedDatabaseTest;
import com.appspring.dataset.UserDataset;
import com.appspring.entity.User;
import com.appspring.entity.model.Role;
import com.appspring.repository.UserRepository;
import com.appspring.rest.dto.UserRqDto;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends ContainerisedDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataset userDataset;

    @BeforeEach
    void setUp() {
        userDataset.createData();
    }

    @AfterEach
    void tearDown() {
        userDataset.removeData();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTest() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.notNullValue()))
                .andExpect(jsonPath("content", Matchers.hasSize(4)))
                .andExpect(jsonPath("totalElements", Matchers.is(4)))
                .andExpect(jsonPath("totalPages", Matchers.is(1)))
                .andExpect(jsonPath("numberOfElements", Matchers.is(4)))
                .andExpect(jsonPath("empty", Matchers.is(false)));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void getAllUserTest() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getByIdTest() throws Exception {
        User user = userRepository.findByLogin("user2")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        mockMvc.perform(get("/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.equalTo(user.getId().intValue())))
                .andExpect(jsonPath("login", Matchers.notNullValue()));
    }

    @Test
    @WithMockUser(username = "user2", roles = "USER")
    void getByIdUserTest() throws Exception {
        User user = userRepository.findByLogin("user2")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        mockMvc.perform(get("/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void createTest() throws Exception {
        UserRqDto userRq = UserRqDto.builder()
                .login("user3")
                .newPassword("111111")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(postJson(userRq, "/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", Matchers.notNullValue()))
                .andExpect(jsonPath("login", Matchers.is(userRq.getLogin())));

    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void createWithoutRoleTest() throws Exception {
        UserRqDto userRq = UserRqDto.builder()
                .login("user3")
                .newPassword("111111")
                .build();

        mockMvc.perform(postJson(userRq, "/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void createLoginRoleTest() throws Exception {
        UserRqDto userRq = UserRqDto.builder()
                .newPassword("111111")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(postJson(userRq, "/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void createPasswordRoleTest() throws Exception {
        UserRqDto userRq = UserRqDto.builder()
                .login("user3")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(postJson(userRq, "/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "admin", password = "111111", roles = "ADMIN")
    void updateTest() throws Exception {
        User user = userRepository.findByLogin("user1")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        UserRqDto userRq = UserRqDto.builder()
                .login(user.getLogin())
                .role(Role.ROLE_ADMIN)
                .build();

        mockMvc.perform(putJson(userRq, "/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.equalTo(user.getId().intValue())))
                .andExpect(jsonPath("login", Matchers.equalTo(user.getLogin())));
    }

    @Test
    @WithMockUser(username = "admin", password = "111111", roles = "ADMIN")
    void deleteTest() throws Exception {
        User user = userRepository.findByLogin("user2")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        mockMvc.perform(delete("/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isAccepted());

        userRepository.findById(user.getId())
                .ifPresentOrElse(value -> assertTrue(value.getDeleted()), () -> {
                    throw new RuntimeException("Пользователь не найден.");
                });
    }

    @Test
    @WithMockUser(username = "admin1", roles = "USER")
    void getCurrentTest() throws Exception {
        mockMvc.perform(get("/users/current"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user1", password = "111111", roles = "USER")
    @Test
    void updateCurrentTest() throws Exception {
        User user = userRepository.findByLogin("user1")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        UserRqDto userRq = UserRqDto.builder()
                .login(user.getLogin())
                .role(Role.ROLE_USER)
                .build();
        mockMvc.perform(putJson(userRq, "/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user1", password = "111111", roles = "USER")
    @Test
    void updatePasswordTest() throws Exception {
        User user = userRepository.findByLogin("user1")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        UserRqDto userRq = UserRqDto.builder()
                .login(user.getLogin())
                .role(Role.ROLE_USER)
                .currentPassword("111111")
                .newPassword("555555")
                .build();
        mockMvc.perform(putJson(userRq, "/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

}