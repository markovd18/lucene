package cz.zcu.kiv.nlp.ir;

import java.io.PrintStream;
import java.util.Collection;

import org.apache.lucene.document.Document;

import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

public class ResultPrinter {

  private final PrintStream printStream;

  public ResultPrinter(final PrintStream printStream) {
    checkNotNull(printStream, "Output print stream");
    this.printStream = printStream;
  }

  public void printDocuments(final Collection<Document> documents) {
    for (final var doc : documents) {
      this.printStream.format("%s (%s) \t %s\n", doc.get("author"), doc.get("date"), doc.get("title"));
    }
  }
}
