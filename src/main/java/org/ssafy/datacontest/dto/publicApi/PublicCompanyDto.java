package org.ssafy.datacontest.dto.publicApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ssafy.datacontest.enums.IndustryType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicCompanyDto {
    @JsonProperty("업체명")
    private String companyName;

    @JsonProperty("업종명(11차)")
    private String companyDescription;

    @JsonProperty("지역")
    private String companyLoc;

    @JsonProperty("업종분류(기보)")
    private String companyField;

    @JsonProperty("간략주소")
    private String simpleAddress;
}
