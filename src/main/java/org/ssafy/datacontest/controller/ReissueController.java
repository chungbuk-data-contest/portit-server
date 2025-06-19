package org.ssafy.datacontest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.service.ReissueService;

@RestController
@RequestMapping("/reissue")
public class ReissueController {
    private final ReissueService reissueService;

    @Autowired
    public ReissueController(ReissueService reissueService){
        this.reissueService = reissueService;
    }

    @PostMapping("")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        reissueService.reissue(request, response);
        return ResponseEntity.ok().build();
    }
}
