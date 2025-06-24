package org.ssafy.datacontest.service;

import java.util.List;

public interface GptService {
    List<String> generateTags(String articleDescription);

    String generateIndustry(String articleDescription);
}
