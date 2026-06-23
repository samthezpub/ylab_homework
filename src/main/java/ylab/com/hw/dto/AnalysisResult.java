package ylab.com.hw.dto;

import java.util.List;

public record AnalysisResult(
    AnalysisInfo analysisInfo,
    List<WordFrequency> words,
    List<FileError> errors
) {
  public record AnalysisInfo(
      String directory,
      int minWordLength,
      int topCount
  ) {
  }
}