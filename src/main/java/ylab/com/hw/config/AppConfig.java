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
  private String dir;
  private Integer minLength;
  private Integer top;
  private String output;
  private String stopwords;
  private boolean help;
}