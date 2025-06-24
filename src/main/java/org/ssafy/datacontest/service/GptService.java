package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.gpt.GptRequest;

import java.util.List;

public interface GptService {
    List<String> generateTags(GptRequest gptRequest);

    String generateIndustry(GptRequest gptRequest);
}
