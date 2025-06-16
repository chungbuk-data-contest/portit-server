package org.ssafy.datacontest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.ssafy.datacontest.dto.publicApi.ApiResponseWrapper;
import org.ssafy.datacontest.dto.publicApi.PublicCompanyDto;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicApiClient {

    @Value("${public-api-d.key}")
//    @Value("${public-api.key}")
    private String serviceKey;

    public List<PublicCompanyDto> fetchData() {
        // RestTemplate : Spring 에서 제공하는 HTTP 요청 도구
        RestTemplate restTemplate = new RestTemplate();

        // GET 요청을 보내고, 응답 JSON을 ApiResponseWrapper 클래스에 자동으로 매핑
        String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

        String fullUrl = "https://api.odcloud.kr/api/15084581/v1/uddi:6a70420a-9773-489a-9adc-becdd7859219"
                + "?page=1&perPage=1000&serviceKey=" + encodedKey;

        URI uri = URI.create(fullUrl);

        log.info("Generated URI: {}", uri);

        ApiResponseWrapper response = restTemplate.getForObject(uri, ApiResponseWrapper.class);

        if (response == null || response.getData() == null) {
            throw new RuntimeException("공공데이터 API 호출 실패 또는 데이터 없음");
        }

        return response.getData();
    }
}
