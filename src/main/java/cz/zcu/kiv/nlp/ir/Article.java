package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public record Article(
    String title,
    String author,
    String date,
    String content) implements Indexable {

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
}