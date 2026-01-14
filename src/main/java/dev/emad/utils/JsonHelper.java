package dev.emad.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.SimpleDateFormat;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author EmadHanif
 */
public class JsonHelper {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper =
        JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
            .changeDefaultPropertyInclusion(
                incl ->
                    incl.withValueInclusion(JsonInclude.Include.NON_NULL)
                        .withContentInclusion(JsonInclude.Include.NON_NULL))
            .build();
  }

  public static String convertToString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JacksonException e) {
      throw new RuntimeException("Failed to convert object to JSON: " + e.getMessage());
    }
  }

  public static String convertToStringPretty(Object object) {
    try {
      return objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JacksonException e) {
      throw new RuntimeException("Failed to convert object to JSON: " + e.getMessage());
    }
  }

  public static <T> T convertToObject(Map<String, Object> body, Class<T> clazz) {
    return objectMapper.convertValue(body, clazz);
  }

  public static <T> T convertToObject(String jsonString, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonString, clazz);
    } catch (JacksonException e) {
      throw new RuntimeException("Failed to convert Json to object: " + e.getMessage());
    }
  }

  public static boolean isValidJson(String jsonString) {
    try {
      objectMapper.readTree(jsonString);
      return true;
    } catch (JacksonException e) {
      return false;
    }
  }
}
