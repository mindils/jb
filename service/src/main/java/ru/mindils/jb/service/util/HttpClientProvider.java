package ru.mindils.jb.service.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientProvider {

  private static final HttpClientProvider INSTANCE = new HttpClientProvider();
  private final HttpClient client;
  private final ObjectMapper mapper;
  private HttpRequest.Builder requestBuilder;

  private HttpClientProvider() {
    this.client = HttpClient.newHttpClient();
    this.mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static HttpClientProvider get(URI uri) {
    INSTANCE.requestBuilder = HttpRequest.newBuilder().uri(uri);
    return INSTANCE;
  }

  public <T> T retrieve(Class<T> responseType) throws IOException, InterruptedException {
    HttpRequest request = requestBuilder.build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return mapper.readValue(response.body(), responseType);
  }

}
