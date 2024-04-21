package ru.mindils.jb.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DetailedVacancyDto {

  private String id;
  private String name;
  private Boolean premium;

  private Map<String, ?> department;

  @JsonProperty("response_letter_required")
  private Boolean responseLetterRequired;

  private AreaDto area;
  private SalaryDto salary;
  private KeyValueDto type;

  //  private String address;

  @JsonProperty("response_url")
  private String responseUrl;

  @JsonProperty("published_at")
  private ZonedDateTime publishedAt;

  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  private Boolean archived;

  @JsonProperty("apply_alternate_url")
  private String applyAlternateUrl;

  private String url;

  @JsonProperty("alternate_url")
  private String alternateUrl;

  private KeyValueDto schedule;

  @JsonProperty("professional_roles")
  private List<Map<String, ?>> professionalRoles;

  private KeyValueDto experience;
  private KeyValueDto employment;

  private BriefEmployerDto employer;

  private Boolean detailed = true;

  private String description;

  @JsonProperty("key_skills")
  private List<KeySkillDto> keySkills;
}
