package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import cz.zcu.kiv.nlp.ir.storage.InMemoryStorage;
import cz.zcu.kiv.nlp.ir.storage.Storage;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class Main {

  private static final String STOPWORDS_DEFAULT_PATH = "stopwords-cs.txt";

  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    StopwordsLoader stopwordsLoader = new StopwordsLoader(STOPWORDS_DEFAULT_PATH);
    final var stopwords = stopwordsLoader.loadStopwords();
    Analyzer analyzer = new CzechAnalyzer(stopwords);

    // 1. create the index
    Directory index = IndexDirectoryType.FILE_BASED.getDirectoryInstance();
    if (!DirectoryReader.indexExists(index)) {
      IndexWriterConfig config = new IndexWriterConfig(analyzer);

      IndexWriter w = new IndexWriter(index, config);
      addDoc(w, "Lucene in Action", "193398817");
      addDoc(w, "Lucene for Dummies", "55320055Z");
      addDoc(w, "Managing Gigabytes", "55063554A");
      addDoc(w, "The Art of Computer Science", "9900333X");
      w.close();

      Storage storage = new InMemoryStorage();
      final var documents = storage.getEntries();
      createIndex(analyzer, index, documents);
    }

    // 2. query
    String querystr = args.length > 0 ? args[0] : "lucene";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser("title", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;

    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    var storedFields = searcher.storedFields();
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = storedFields.document(docId);
      System.out.println((i + 1) + ". " + d.get("author") + "\t" + d.get("title"));
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
  }

  private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField("isbn", isbn, Field.Store.YES));
    w.addDocument(doc);
  }

  private static void createIndex(Analyzer analyzer, Directory index, Collection<Indexable> documents) {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    try (IndexWriter w = new IndexWriter(index, config)) {
      w.addDocuments(documents.stream().map(Indexable::toDocument).collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
