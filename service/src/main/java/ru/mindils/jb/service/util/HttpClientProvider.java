package ru.mindils.jb.service.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Service;
import ru.mindils.jb.sync.dto.ResponseWrapperSync;

@Service
public class HttpClientProvider {

  private final HttpClient client;
  private final ObjectMapper mapper;

  private HttpClientProvider() {
    this.client = HttpClient.newHttpClient();
    this.mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public <T> ResponseWrapperSync<T> retrieve(URI uri, Class<T> responseType)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    T data = mapper.readValue(response.body(), responseType);

    return ResponseWrapperSync.<T>builder().data(data).response(response).build();
  }
}
