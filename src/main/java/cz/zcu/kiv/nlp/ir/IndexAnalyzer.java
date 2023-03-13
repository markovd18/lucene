package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import static cz.zcu.kiv.nlp.ir.ValidationUtils.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexAnalyzer {

  public static final int DEFAULT_HITS_PER_PAGE = 10;

  private final IndexSearcher searcher;
  private final Query query;
  private final int hitsPerPage;
  private ScoreDoc[] nextPage;

  public IndexAnalyzer(final IndexSearcher indexSearcher, final Query query, final int hitsPerPage) throws IOException {
    validate(indexSearcher, query, hitsPerPage);
    this.query = query;
    this.hitsPerPage = hitsPerPage;

    this.searcher = indexSearcher;
    TopDocs docs = searcher.searchAfter(null, query, this.hitsPerPage);
    nextPage = docs.scoreDocs;
  }

  public IndexAnalyzer(final IndexSearcher indexSearcher, final Query query) throws IOException {
    this(indexSearcher, query, DEFAULT_HITS_PER_PAGE);
  }

  public boolean hasNextPage() {
    return nextPage.length > 0;
  }

  public List<Document> nextPage() throws IOException {
    final StoredFields storedFields = searcher.storedFields();
    final List<Document> result = new ArrayList<>(nextPage.length);
    for (final var hit : nextPage) {
      result.add(storedFields.document(hit.doc));
    }

    final var nextTopDocs = searcher.searchAfter(nextPage[nextPage.length - 1], query, hitsPerPage);
    this.nextPage = nextTopDocs.scoreDocs;
    return result;
  }

  private void validate(final IndexSearcher indexSearcher, final Query query, final int hitsPerPage) {
    checkNotNull(indexSearcher, "Index searcher");
    checkNotNull(query, "Query");

    if (hitsPerPage <= 0) {
      throw new IllegalArgumentException("Hits per page has to be a positive integer");
    }
  }
}
