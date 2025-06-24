package org.ssafy.datacontest.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ssafy.datacontest.dto.gpt.GptRequest;
import org.ssafy.datacontest.service.GptService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GptServiceImpl implements GptService {

    @Value("${gpt.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<String> generateTags(GptRequest gptRequest) {
        String prompt = String.format("""
            다음 작품 설명을 기반으로 2~4글자의 태그 2개를 생성해줘. 해시태그 기호 없이, 콤마로 구분해서 응답해줘.
            작품 설명: %s
            결과 예시: 혁신,인공지능
            """, gptRequest.getDescription());

        String response = callGptApi(prompt);
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .toList();
    }

    @Override
    public String generateIndustry(GptRequest gptRequest) {
        String prompt = String.format("""
            다음 작품 설명을 기반으로 가장 관련 있는 산업 분야 하나만 간단하게 말해줘.
            기업 산업분야는 다음과 같음 : IT/소프트웨어,디자인/콘텐츠,영상/방송/미디어,교육/에듀테크,의료/헬스케어,제조/기계/전자,유통/커머스,건설/부동산,문화/예술/공연,환경/에너지,공공기관/비영리/행정
            작품 설명: %s
            """, gptRequest.getDescription());

        return callGptApi(prompt).trim();
    }

    private String callGptApi(String prompt) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> message = Map.of(
                "model", "gpt-4o",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        JsonNode root = null;
        try {
            root = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.info("error parsing GPT response: {}", e.getMessage());
        }

        return root
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText();
    }
}