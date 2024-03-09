package ru.mindils.jb.sync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BriefEmployerDto {

    private String id;
    private String name;

    private Boolean detailed = false;
}
