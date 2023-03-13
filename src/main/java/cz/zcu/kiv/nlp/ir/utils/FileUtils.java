package cz.zcu.kiv.nlp.ir.utils;

import static cz.zcu.kiv.nlp.ir.utils.ValidationUtils.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {

  /**
   * Read lines from the stream; lines are trimmed and empty lines are ignored.
   *
   * @param inputStream stream
   * @return list of lines
   */
  public static List<String> readTXTFile(final InputStream inputStream) {
    checkNotNull(inputStream, "Input stream");

    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      List<String> result = new LinkedList<>();

      String line;
      while ((line = br.readLine()) != null) {
        final var trimmedLine = line.trim();
        if (!trimmedLine.isEmpty()) {
          result.add(trimmedLine);
        }
      }

      return result;
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
