package org.ssafy.datacontest.dto.publicApi;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponseWrapper {
    public int page;
    public int perPage;
    public int totalCount;
    public int currentCount;
    public int matchCount;
    public List<PublicCompanyDto> data;
}
