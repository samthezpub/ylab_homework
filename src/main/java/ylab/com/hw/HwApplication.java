package ylab.com.hw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ylab.com.hw.config.AppConfig;
import ylab.com.hw.dto.AnalysisResult;
import ylab.com.hw.formatter.ConsoleFormatter;
import ylab.com.hw.formatter.JsonFormatter;
import ylab.com.hw.service.HelpPrinter;
import ylab.com.hw.service.TextAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class HwApplication implements ApplicationRunner {
  private final TextAnalyzer textAnalyzer;
  private final ConsoleFormatter consoleFormatter;
  private final JsonFormatter jsonFormatter;

  public static void main(String[] args) {
    boolean helpRequested = Arrays.asList(args).contains("--help");
    if (helpRequested) {
      // печатаем help без поднятия Spring-контекста вообще
      new HelpPrinter().print();
      return;
    }

    String[] filteredArgs = Arrays.stream(args)
        .filter(arg -> !arg.equals("--help"))
        .toArray(String[]::new);

    SpringApplication.run(HwApplication.class, filteredArgs);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    AppConfig config;
    try {
      config = parseArgs(args);
    } catch (IllegalArgumentException e) {
      System.err.println("Ошибка: " + e.getMessage());
      System.err.println("Запустите с --help для справки");
      return;
    }

    Path dir = Path.of(config.getDir());
    if (!Files.isDirectory(dir)) {
      System.err.println("Ошибка: папка не существует или недоступна: " + config.getDir());
      log.error("Директория не найдена: {}", config.getDir());
      return;
    }

    AnalysisResult result = textAnalyzer.analyze(config);

    if (config.getOutput() != null) {
      try {
        String json = jsonFormatter.format(result);
        Files.writeString(Path.of(config.getOutput()), json);
        System.out.println("Результат сохранён в: " + config.getOutput());
        log.info("JSON результат записан в файл: {}", config.getOutput());
      } catch (IOException e) {
        System.err.println("Ошибка записи файла: " + e.getMessage());
        log.error("Не удалось записать результат в файл: {}", config.getOutput(), e);
      }
    } else {
      System.out.println(consoleFormatter.format(result));
    }
  }

  private AppConfig parseArgs(ApplicationArguments args) {
    AppConfig config = new AppConfig();

    config.setDir(required(args, "dir"));
    config.setMinLength(requiredInt(args, "min-length"));
    config.setTop(requiredInt(args, "top"));
    config.setOutput(optional(args, "output"));
    config.setStopwords(optional(args, "stopwords"));

    if (config.getMinLength() < 1) {
      throw new IllegalArgumentException("--min-length должен быть >= 1");
    }
    if (config.getTop() < 1) {
      throw new IllegalArgumentException("--top должен быть >= 1");
    }

    return config;
  }

  private String required(ApplicationArguments args, String name) {
    String value = optional(args, name);
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("--" + name + " обязателен");
    }
    return value;
  }

  private String optional(ApplicationArguments args, String name) {
    List<String> values = args.getOptionValues(name);
    return values == null || values.isEmpty() ? null : values.get(0);
  }

  private int requiredInt(ApplicationArguments args, String name) {
    String value = required(args, name);
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("--" + name + " должен быть целым числом");
    }
  }
}
