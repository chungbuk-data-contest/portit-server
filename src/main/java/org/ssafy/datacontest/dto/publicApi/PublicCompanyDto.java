package org.ssafy.datacontest.dto.publicApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicCompanyDto {
    @JsonProperty("업체명")
    public String 업체명;

    @JsonProperty("업종명(11차)")
    public String 업종명;

    @JsonProperty("지역")
    public String 지역;

    @JsonProperty("업종분류(기보)")
    public String 업종분류;
}
