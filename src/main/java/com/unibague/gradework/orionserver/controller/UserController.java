package com.unibague.gradework.orionserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController
{
    @PostMapping(value="demo")
    public String login()
    {
        return "bienvenido mi rey";
    }
}
