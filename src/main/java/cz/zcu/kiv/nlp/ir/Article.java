package cz.zcu.kiv.nlp.ir;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public record Article(
    String title,
    String author,
    String date,
    String content) implements Indexable {

  private static final String datePattern = "\\d\\d?\\.\\ [\\wú]+\\ \\d\\d?:\\d\\d";

  @Override
  public Document toDocument() {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));

    doc.add(new TextField("author", author, Field.Store.YES));

    // https://cwiki.apache.org/confluence/display/LUCENE/IndexingDateFields
    // https://stackoverflow.com/questions/5495645/indexing-and-searching-date-in-lucene
    // final var date = DateTools.dateToString(article.date(),
    // DateTools.Resolution.SECOND);
    doc.add(new StringField("date", date, Field.Store.YES));
    doc.add(new TextField("content", content, Field.Store.YES));
    return doc;
  }

  public static Article fromTXTFile(final List<String> lines) {
    if (lines.size() < 4) {
      throw new IllegalArgumentException("Loaded article needs to have a title, author, date and content");
    }

    final String title = lines.get(0);

    String date = null;
    String author = null;
    StringBuilder sb = new StringBuilder();
    for (final String line : lines) {
      if (date == null) {
        date = line.matches(datePattern) ? line : null;
        continue;
      }

      if (author == null) {
        author = line;
        continue;
      }

      sb.append(line);
    }

    if (date == null) {
      throw new IllegalArgumentException("Date of the article was not found");
    }

    if (author == null) {
      throw new IllegalArgumentException("Author of the article was not found");
    }

    final String content = sb.toString();
    return new Article(title, author, date, content);
  }
}