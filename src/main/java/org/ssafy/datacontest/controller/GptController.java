package org.ssafy.datacontest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.datacontest.dto.gpt.GptRequest;
import org.ssafy.datacontest.service.GptService;

import java.util.List;

@Tag(name = "GPT")
@RestController
@RequestMapping("/gpt")
public class GptController {
    private final GptService gptService;

    @Autowired
    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    @Operation(summary = "작품 기반 태그 생성", description = "작품 설명을 기반으로 2개의 태그를 생성")
    @PostMapping("/tags")
    public ResponseEntity<List<String>> generateTags(@RequestBody GptRequest gptRequest) {
        List<String> tags = gptService.generateTags(gptRequest);
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "작품 설명 기반 산업 분야 반환", description = "작품 설명 기반 산업 분야 반환, 활용해서 기업 추천 가능")
    @PostMapping("/industry")
    public ResponseEntity<String> Industry(@RequestBody GptRequest gptRequest) {
        String industry = gptService.generateIndustry(gptRequest);
        return ResponseEntity.ok(industry);
    }
}
