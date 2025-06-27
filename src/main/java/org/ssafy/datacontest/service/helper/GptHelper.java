package org.ssafy.datacontest.service.helper;

import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.gpt.GptRequest;
import org.ssafy.datacontest.util.GptUtil;

@Component
public class GptHelper {

    private final GptUtil gptUtil;

    public GptHelper(GptUtil gptUtil) {
        this.gptUtil = gptUtil;
    }

    public String generateIndustry(GptRequest gptRequest) {
        String prompt = String.format("""
        다음 작품 설명을 기반으로 가장 관련 있는 산업 분야 하나를 골라줘.
        응답은 반드시 아래 enum 중 하나의 **영문 이름(대문자)**만 리턴해줘.
        
        산업 분야(enum 이름):
        - IT_SOFTWARE
        - DESIGN_CONTENTS
        - MEDIA
        - EDUCATION_EDUTECH
        - MEDICAL_HEALTH
        - MANUFACTURING_ELECTRONICS
        - COMMERCE
        - CONSTRUCTION_REAL_ESTATE
        - CULTURE_ART
        - ENVIRONMENT_ENERGY
        - PUBLIC_ORGANIZATION
        
        작품 설명: %s
        """, gptRequest.getDescription());

        return gptUtil.callGpt(prompt).trim();
    }
}
