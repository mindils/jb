package ru.mindils.jb.service.util;

import java.util.Arrays;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@UtilityClass
public class ControllerUtils {

  public static String toggleSortDirection(Order order) {
    if (order == null) {
      return "asc";
    }

    if (order.getDirection().toString().equalsIgnoreCase("asc")) {
      return "desc";
    }
    return "asc";
  }

  public static String createQueryStringForParams(
      Map<String, String[]> parameterMap, String... excludeKeys) {
    UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();

    for (String key : excludeKeys) {
      uriBuilder.replaceQueryParam(key);
    }

    boolean hasQueryParams = false;

    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
      String key = entry.getKey();
      String[] values = entry.getValue();

      if (!Arrays.asList(excludeKeys).contains(key)) {
        uriBuilder.replaceQueryParam(key, (Object[]) values);
        hasQueryParams = true;
      }
    }
    return uriBuilder.toUriString() + (hasQueryParams ? "&" : "?");
  }
}
