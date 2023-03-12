package cz.zcu.kiv.nlp.ir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public enum IndexDirectoryType {
  IN_MEMORY("memory", () -> new ByteBuffersDirectory()),
  FILE_BASED("file", () -> {
    try {
      return FSDirectory.open(Path.of("index"));
    } catch (final IOException e) {
      throw new RuntimeException("Error while openning index directory", e);
    }
  });

  private final String name;
  private final Supplier<Directory> directoryInstanceSupplier;

  IndexDirectoryType(final String name, final Supplier<Directory> directoryInstanceSupplier) {
    this.name = name;
    this.directoryInstanceSupplier = directoryInstanceSupplier;
  }

  public static Optional<IndexDirectoryType> from(final String name) {
    if (name == null) {
      return Optional.empty();
    }

    return Arrays.stream(IndexDirectoryType.values())
        .filter(type -> type.name.equals(name))
        .findFirst();
  }

  public Directory getDirectoryInstance() {
    return directoryInstanceSupplier.get();
  }
}
