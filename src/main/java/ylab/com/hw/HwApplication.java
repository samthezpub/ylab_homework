package ylab.com.hw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class HwApplication implements CommandLineRunner {

  private final AppConfig config;
  private final TextAnalyzer textAnalyzer;
  private final ConsoleFormatter consoleFormatter;
  private final JsonFormatter jsonFormatter;
  private final HelpPrinter helpPrinter;

  public static void main(String[] args) {
    SpringApplication.run(HwApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    if (config.isHelp()) {
      helpPrinter.print();
      return;
    }

    if (!validateParams()) {
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

  private boolean validateParams() {
    if (config.getDir() == null || config.getDir().isBlank()) {
      System.err.println("Ошибка: --dir обязателен");
      printUsage();
      return false;
    }
    if (config.getMinLength() == null) {
      System.err.println("Ошибка: --min-length обязателен");
      printUsage();
      return false;
    }
    if (config.getMinLength() < 1) {
      System.err.println("Ошибка: --min-length должен быть >= 1");
      return false;
    }
    if (config.getTop() == null) {
      System.err.println("Ошибка: --top обязателен");
      printUsage();
      return false;
    }
    if (config.getTop() < 1) {
      System.err.println("Ошибка: --top должен быть >= 1");
      return false;
    }
    return true;
  }

  private void printUsage() {
    System.err.println("Запустите с --help для справки");
  }
}
