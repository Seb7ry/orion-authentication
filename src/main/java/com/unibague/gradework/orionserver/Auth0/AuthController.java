package com.unibague.gradework.orionserver.Auth0;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController
{
    @PostMapping(value="login")
    public String login()
    {
        return "me estoy logueando";
    }

    @PostMapping(value="register")
    public String register()
    {
        return "me estoy registrado";
    }
}
