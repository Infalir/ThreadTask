package main.com.gavruseva.task3.reader;

import main.com.gavruseva.task3.exception.MultiThreadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataReader {
  private static final Logger logger = LogManager.getLogger();
  public static List<String> fileReading(String path) throws MultiThreadException {
    if (path == null) {
      logger.error("Path is null");
      throw new MultiThreadException("Path is null");
    }
    Path filePath = Paths.get(path);
    List<String> lines;
    try (Stream<String> streamLines = Files.lines(filePath)) {
      lines = streamLines.collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("File in {} not found", path, e);
      throw new MultiThreadException("File not found", e);
    }
    return lines;
  }
}
