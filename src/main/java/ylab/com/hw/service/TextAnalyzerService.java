package ylab.com.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ylab.com.hw.config.AppConfig;
import ylab.com.hw.dto.AnalysisResult;
import ylab.com.hw.dto.FileError;
import ylab.com.hw.dto.WordFrequency;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Slf4j
@Service
public class TextAnalyzerService implements TextAnalyzer {
  private final ObjectMapper mapper;

  public TextAnalyzerService(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public AnalysisResult analyze(AppConfig config) {
    Path dir = Path.of(config.getDir());
    log.info("Начало анализа директории: {}", dir);

    Set<String> stopwords = loadStopWords(config.getStopwords());
    log.debug("Загружено стоп-слов: {}", stopwords.size());

    Map<String, Long> freq = new HashMap<>();
    List<FileError> errors = new CopyOnWriteArrayList<>();

    try (Stream<Path> files = Files.list(dir)) {
      List<Path> txtFiles = files
          .filter(p -> p.toString().endsWith(".txt"))
          .toList();

      log.info("Найдено .txt файлов: {}", txtFiles.size());

      txtFiles.forEach(file ->
          processFile(file, config.getMinLength(), stopwords, freq, errors)
      );
    } catch (IOException e) {
      log.error("Ошибка при чтении директории: {}", dir, e);
      errors.add(new FileError(dir.toString(), e.getMessage()));
    }

    List<WordFrequency> top = freq.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(config.getTop())
        .map(e -> new WordFrequency(e.getKey(), e.getValue()))
        .toList();

    log.info("Анализ завершён. Уникальных слов: {}, топ: {}", freq.size(), top.size());

    return new AnalysisResult(
        new AnalysisResult.AnalysisInfo(
            config.getDir(),
            config.getMinLength(),
            config.getTop()
        ),
        top,
        errors
    );
  }

  private void processFile(Path file, int minLength, Set<String> stopwords,
                           Map<String, Long> freq, List<FileError> errors) {
    log.debug("Обработка файла: {}", file.getFileName());
    try {
      String content = Files.readString(file, StandardCharsets.UTF_8);
      if (content.isBlank()) {
        log.warn("Файл пустой: {}", file.getFileName());
        return;
      }
      String[] words = content.toLowerCase().split("\\P{L}+");
      for (String word : words) {
        if (!word.isEmpty() && word.length() >= minLength && !stopwords.contains(word)) {
          freq.merge(word, 1L, Long::sum);
        }
      }
    } catch (IOException e) {
      log.error("Ошибка чтения файла: {}", file, e);
      errors.add(new FileError(file.getFileName().toString(), e.getMessage()));
    }
  }


  private Set<String> loadStopWords(String path) {
    if (path == null) return Set.of();
    try {
      List<String> lines = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
      return new HashSet<>(lines.stream()
          .map(String::toLowerCase)
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .toList());
    } catch (IOException e) {
      log.error("Не удалось загрузить стоп-слова: {}", path, e);
      return Set.of();
    }
  }
}
