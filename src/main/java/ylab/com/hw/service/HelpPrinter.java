package ylab.com.hw.service;

import org.springframework.stereotype.Component;

@Component
public class HelpPrinter {
  public void print() {
    System.out.println("""
        Использование:
          java -jar text-analyzer.jar --dir=<path> --min-length=<n> --top=<n> [опции]
        
        Обязательные параметры:
          --dir=<path>          Путь к папке с .txt файлами
          --min-length=<n>      Минимальная длина слова (целое число)
          --top=<n>             Количество наиболее частых слов для вывода
        
        Опциональные параметры:
          --output=<path>       Путь к файлу для сохранения результата в формате JSON
          --stopwords=<path>    Путь к файлу со списком стоп-слов (по одному на строку)
          --help                Показать эту справку
        
        Примеры:
          java -jar text-analyzer.jar --dir=./texts --min-length=5 --top=10
          java -jar text-analyzer.jar --dir=./texts --min-length=4 --top=20 --output=./result.json
          java -jar text-analyzer.jar --dir=./texts --min-length=3 --top=5 --stopwords=./stop.txt
        """);
  }
}
