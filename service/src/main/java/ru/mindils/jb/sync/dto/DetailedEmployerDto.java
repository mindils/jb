package ru.mindils.jb.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedEmployerDto {

  private String id;
  private String name;
  private String description;

  private Boolean detailed = true;

  @JsonProperty("accredited_it_employer")
  private Boolean accreditedItEmployer;

  private String type;

  @JsonProperty("site_url")
  private String siteUrl;

  @JsonProperty("alternate_url")
  private String alternateUrl;

  @JsonProperty("logo_urls")
  private LogoUrlsDto logoUrls;

  private AreaDto area;

  private List<Map<String, ?>> industries;
}
