package com.appspring.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
@Api(tags = "Индекс")
public class IndexController {

    @ApiOperation("О приложении")
    @GetMapping
    public Map<String,String> indexUi() {
        return Map.of("about", "App Spring");
    }

}


