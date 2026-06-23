package ylab.com.hw.formatter;

import org.springframework.stereotype.Component;
import ylab.com.hw.dto.AnalysisResult;
import ylab.com.hw.dto.WordFrequency;

import java.util.List;

@Component("consoleFormatter")
public class ConsoleFormatter implements ResultFormatter {

  @Override
  public String format(AnalysisResult result) {
    StringBuilder sb = new StringBuilder();

    List<WordFrequency> words = result.words();
    for (int i = 0; i < words.size(); i++) {
      WordFrequency wf = words.get(i);
      sb.append(String.format("%d. %s - %d%n", i + 1, wf.word(), wf.count()));
    }

    if (!result.errors().isEmpty()) {
      sb.append("\nОшибки при чтении файлов:\n");
      result.errors().forEach(e ->
          sb.append(String.format("  [!] %s: %s%n", e.file(), e.message()))
      );
    }

    return sb.toString().stripTrailing();
  }
}
