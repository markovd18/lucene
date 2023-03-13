package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import cz.zcu.kiv.nlp.ir.storage.FSStorage;
import cz.zcu.kiv.nlp.ir.storage.Storage;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

  private static final String STOPWORDS_DEFAULT_PATH = "stopwords-cs.txt";
  private static final String STORAGE_DFAULT_PATH = "storage";

  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    StopwordsLoader stopwordsLoader = new StopwordsLoader(STOPWORDS_DEFAULT_PATH);
    final var stopwords = stopwordsLoader.loadStopwords();
    Analyzer analyzer = new CzechAnalyzer(stopwords);

    // 1. create the index
    Directory index = IndexDirectoryType.FILE_BASED.getDirectoryInstance();
    if (!DirectoryReader.indexExists(index)) {
      Storage<Article> storage = new FSStorage<Article>(STORAGE_DFAULT_PATH, Article::fromTXTFile);
      final var documents = storage.getEntries();
      createIndex(analyzer, index, documents);
    }

    // 2. query
    String querystr = args.length > 0 ? args[0] : "lucene";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser("title", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 1;
    try (final IndexReader reader = DirectoryReader.open(index)) {
      final var printer = new ResultPrinter(System.out);
      final var searcher = new IndexAnalyzer(new IndexSearcher(reader), q, hitsPerPage);
      try (Scanner scanner = new Scanner(System.in)) {

        printer.printDocuments(searcher.nextPage());
        while (searcher.hasNextPage()) {
          System.out.println("--- MORE");
          scanner.nextLine();

          printer.printDocuments(searcher.nextPage());
        }
      }
    }

  }

  private static void createIndex(Analyzer analyzer, Directory index, Collection<? extends Indexable> documents) {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    try (IndexWriter w = new IndexWriter(index, config)) {
      w.addDocuments(documents.stream().map(Indexable::toDocument).collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
