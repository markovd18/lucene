package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.document.Document;

public interface Indexable {
  Document toDocument();
}
