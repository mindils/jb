package ru.mindils.jb.sync.dto;

import java.net.http.HttpResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseWrapperSync<T>   {

  private final T data;
  private final HttpResponse<String> response;
}
