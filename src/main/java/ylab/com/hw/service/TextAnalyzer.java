package ylab.com.hw.service;

import ylab.com.hw.config.AppConfig;
import ylab.com.hw.dto.AnalysisResult;

public interface TextAnalyzer {
  AnalysisResult analyze(AppConfig config);
}
