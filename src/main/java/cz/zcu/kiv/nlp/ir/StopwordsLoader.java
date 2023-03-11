package cz.zcu.kiv.nlp.ir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.util.IOUtils;

public record StopwordsLoader(
    String path) {

  public CharArraySet loadStopwords() {
    final var inputStream = getClass().getClassLoader().getResourceAsStream(path);

    try {
      return WordlistLoader.getWordSet(IOUtils.getDecodingReader(inputStream, StandardCharsets.UTF_8),
          "#");
    } catch (IOException e) {
      // default set should always be present as it is part of the
      // distribution (JAR)
      throw new RuntimeException("Unable to load default stopword set", e);
    }
  }
}
