package ru.mindils.jb.sync.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class BriefVacancyDto {

  private String id;
  private String name;
  private Boolean premium;
  private Map<String, ?> department;

  @JsonProperty("response_letter_required")
  private Boolean responseLetterRequired;

  private AreaDto area;
  private SalaryDto salary;
  private KeyValueDto type;

  @JsonProperty("response_url")
  private String responseUrl;

  @JsonProperty("published_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  private Instant publishedAt;

  @JsonProperty("created_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  private Instant createdAt;

  private Boolean archived;

  @JsonProperty("apply_alternate_url")
  private String applyAlternateUrl;

  private String url;

  @JsonProperty("alternate_url")
  private String alternateUrl;

  private KeyValueDto schedule;

  private BriefEmployerDto employer;

  @JsonProperty("professional_roles")
  private List<Map<String, ?>> professionalRoles;

  private KeyValueDto experience;
  private KeyValueDto employment;

  private Boolean detailed = false;
}
