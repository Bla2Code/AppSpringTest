package com.appspring.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.annotation.PostConstruct;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Component
public class MockMvcHelper {

    private static ObjectMapper objectMapper;

    @Autowired
    private ObjectMapper objMapper;

    @PostConstruct
    private void initStaticObjectMapper() {
        objectMapper = this.objMapper;
    }

    public static MockHttpServletRequestBuilder putJson(Object body, String urlTemplate, Object... uriVars) {
        try {
            return put(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MockHttpServletRequestBuilder patchJson(Object body, String urlTemplate, Object... uriVars) {
        try {
            return patch(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MockHttpServletRequestBuilder postJson(Object body, String urlTemplate, Object... uriVars) {
        try {
            return post(urlTemplate, uriVars).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultMatcher iterableSize(String expression, long size) {
        return ResultMatcher.matchAll(
                jsonPath(expression, notNullValue()),
                jsonPath(expression, iterableWithSize((int) size))
        );
    }

    @SneakyThrows
    public static String asJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }
}
