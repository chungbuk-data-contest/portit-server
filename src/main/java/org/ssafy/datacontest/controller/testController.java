package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.hibernate.annotations.Parameter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class testController {

    @GetMapping("/")
    @Operation(
            summary = "테스트용"
    )
    public String test() {
        return "welcome to my world";
    }

}
