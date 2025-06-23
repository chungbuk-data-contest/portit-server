package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.service.ReissueService;

@Tag(name = "토큰 재발급", description = "액세스 토큰 만료 시 리프레시 토큰으로 재발급.")
@RestController
@RequestMapping("/reissue")
public class ReissueController {
    private final ReissueService reissueService;

    @Autowired
    public ReissueController(ReissueService reissueService){
        this.reissueService = reissueService;
    }

    @PostMapping("")
    @Operation(
            summary = "액세스 토큰 재발급",
            description = "리프레시 토큰이 유효할 경우, 새로운 액세스 토큰 반환. 쿠키에 리프레쉬 토큰 담아 보내야 함"
    )
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        reissueService.reissue(request, response);
        return ResponseEntity.ok().build();
    }
}
