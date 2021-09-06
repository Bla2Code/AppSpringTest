package com.appspring.rest;

import com.appspring.ContainerisedDatabaseTest;
import com.appspring.entity.User;
import com.appspring.entity.model.Role;
import com.appspring.repository.UserRepository;
import com.appspring.rest.dto.UserRqDto;
import com.appspring.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser(username = "admin", password = "admin", roles = "admin")
class UserControllerTest extends ContainerisedDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
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
    void getByIdTest() throws Exception {
        User user = userRepository.findByLogin("user")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));
        mockMvc.perform(get("/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.equalTo(user.getId().intValue())))
                .andExpect(jsonPath("username", Matchers.notNullValue()));
    }

    @Test
    void createTest() throws Exception {
        UserRqDto userRq = UserRqDto.builder()
                .login("user1")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(postJson(userRq, "/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", Matchers.notNullValue()))
                .andExpect(jsonPath("login", Matchers.is(userRq.getLogin())));

    }

    @Test
    void updateTest() throws Exception {
        User user = userRepository.findByLogin("user1")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

//        ServiceProvider serviceProvider = userDataset.createServiceProvider();

        UserRqDto userRq = UserRqDto.builder()
                .login(user.getLogin())
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(putJson(userRq, "/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.equalTo(user.getId().intValue())))
                .andExpect(jsonPath("login", Matchers.equalTo(user.getLogin())));
    }

    @Test
    void deleteTest() throws Exception {
        User user = userRepository.findByLogin("user")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        mockMvc.perform(delete("/users/" + user.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isAccepted());
    }

    @WithMockUser(username = "admin", roles = "user")
    @Test
    void getCurrentTest() throws Exception {
        mockMvc.perform(get("/users/current"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", roles = "user")
    @Test
    void updateCurrentTest() throws Exception {
        User user = userRepository.findByLogin("user1")
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        UserRqDto userRq = UserRqDto.builder()
                .login(user.getLogin())
                .role(Role.ROLE_USER)
                .build();
        mockMvc.perform(putJson(userRq, "/users/current"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

}