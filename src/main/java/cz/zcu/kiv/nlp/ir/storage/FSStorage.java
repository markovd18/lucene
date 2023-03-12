package cz.zcu.kiv.nlp.ir.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import cz.zcu.kiv.nlp.ir.FileUtils;
import cz.zcu.kiv.nlp.ir.Indexable;

public class FSStorage<TDocument extends Indexable> implements Storage<TDocument> {

  private final String path;
  private final Function<List<String>, TDocument> factory;

  public FSStorage(final String path, final Function<List<String>, TDocument> factory) {
    this.path = path;
    this.factory = factory;
  }

  @Override
  public Set<TDocument> getEntries() {
    File directory = new File(path);
    if (directory.exists() && directory.isDirectory()) {
      return loadDocumentsFromStorage(directory);
    }

    return Collections.emptySet();
  }

  private Set<TDocument> loadDocumentsFromStorage(final File directory) {
    return Arrays.stream(directory.listFiles())
        .map(entry -> {
          try {
            return FileUtils.readTXTFile(new FileInputStream(entry));
          } catch (FileNotFoundException e) {
            return new ArrayList<String>();
          }
        })
        .map(lines -> factory.apply(lines)).collect(Collectors.toSet());
  }

}
