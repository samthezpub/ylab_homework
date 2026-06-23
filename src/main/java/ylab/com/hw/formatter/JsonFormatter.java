package ylab.com.hw.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ylab.com.hw.dto.AnalysisResult;

@Slf4j
@Component("jsonFormatter")
@RequiredArgsConstructor
public class JsonFormatter implements ResultFormatter {

  private final ObjectMapper objectMapper;

  @Override
  public String format(AnalysisResult result) {
    try {
      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      log.error("Ошибка сериализации результата в JSON", e);
      return "{}";
    }
  }
}