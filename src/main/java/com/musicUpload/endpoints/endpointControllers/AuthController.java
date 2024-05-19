package com.musicUpload.endpoints.endpointControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String auth(){
        return "login";
    }
}
