package ylab.com.hw.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "")
public class AppConfig {
  @NotBlank(message = "--dir не указан")
  private String dir;

  @NotNull(message = "--min-length не указан")
  private Integer minLength;

  @NotNull(message = "--top не указан")
  private Integer top;

  private String output; // Опциональный параметр
  private String stopwords; // Опциональный параметр
}
