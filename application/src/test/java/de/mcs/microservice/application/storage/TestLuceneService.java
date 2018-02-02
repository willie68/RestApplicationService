/**
 * 
 */
package de.mcs.microservice.application.storage;

import static org.dizitart.no2.exceptions.ErrorMessage.errorMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.exceptions.IndexingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.Monitor;
import de.mcs.jmeasurement.exception.RendererMustNotBeNullException;
import de.mcs.jmeasurement.renderer.DefaultHTMLRenderer;
import de.mcs.utils.Files;

/**
 * @author w.klaas
 *
 */
public class TestLuceneService {

  private LuceneService service;
  private static File baseDir;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    baseDir = new File("tmp");
    Files.remove(baseDir, true);
    assertTrue(baseDir.mkdirs());
    assertTrue(baseDir.exists());
  }

  @AfterClass
  public static void afterClass() throws IOException, RendererMustNotBeNullException {
    String report = MeasureFactory.getReport(new DefaultHTMLRenderer());
    Files.writeStringToFile(new File("report.html"), report);
  }

  @Before
  public void before() {
    service = new LuceneService(baseDir);
  }

  @After
  public void after() {
    service.clear();
    service.close();
  }

  @Test
  public void test1() {
    NitriteId id = NitriteId.newId();
    service.createIndex(id, "field1", "text1");
    Set<NitriteId> searchByIndex = service.searchByIndex("field1", "text1");
    assertEquals(1, searchByIndex.size());

    service.updateIndex(id, "field2", "text2");
    searchByIndex = service.searchByIndex("field1", "text1");
    assertEquals(1, searchByIndex.size());

    searchByIndex = service.searchByIndex("field2", "text2");
    assertEquals(1, searchByIndex.size());

  }

  @Test
  public void test10() {

    System.out.println("adding 10 Documents to lucene.");
    List<NitriteId> list = new ArrayList<>();
    for (int i = 1; i < 11; i++) {
      System.out.print('.');
      NitriteId id = NitriteId.newId();
      list.add(id);
      Monitor monitor = MeasureFactory.start("createIndex");
      try {
        service.createIndex(id, "field1", "text1");
      } finally {
        monitor.stop();
      }
    }

    Set<NitriteId> searchByIndex;
    Monitor monitor = MeasureFactory.start("searchIndex1");
    try {
      searchByIndex = service.searchByIndex("field1", "text1");
    } finally {
      monitor.stop();
    }
    assertEquals(10, searchByIndex.size());

    int i = 0;
    for (NitriteId id : list) {
      monitor = MeasureFactory.start("updateIndex");
      try {
        service.updateIndex(id, "field2", "text2");
      } finally {
        monitor.stop();
      }
    }

    monitor = MeasureFactory.start("searchIndex11");
    try {
      searchByIndex = service.searchByIndex("field1", "text1");
    } finally {
      monitor.stop();
    }
    assertEquals(10, searchByIndex.size());

    monitor = MeasureFactory.start("searchIndex2");
    try {
      searchByIndex = service.searchByIndex("field2", "text2");
    } finally {
      monitor.stop();
    }
    assertEquals(10, searchByIndex.size());

    System.out.println();
    System.out.println("updating 10 Documents to lucene.");

    for (NitriteId id : list) {
      System.out.print('.');
      service.updateIndex(id, "field2", "text3");
    }
    searchByIndex = service.searchByIndex("field2", "text3");
    assertEquals(10, searchByIndex.size());
    System.out.println();

    list.clear();
    System.out.println("updating 10 new Documents to lucene.");
    for (i = 1; i < 11; i++) {
      System.out.print('.');
      NitriteId id = NitriteId.newId();
      list.add(id);
      service.updateIndex(id, "field1", "text11");
    }
    searchByIndex = service.searchByIndex("field1", "text11");
    assertEquals(10, searchByIndex.size());

    for (NitriteId id : list) {
      service.updateIndex(id, "field2", "text12");
    }
    searchByIndex = service.searchByIndex("field1", "text11");
    assertEquals(10, searchByIndex.size());

    searchByIndex = service.searchByIndex("field2", "text12");
    assertEquals(10, searchByIndex.size());

    System.out.println();
  }

  private static final String CONTENT_ID = "content_id";
  private static final int MAX_SEARCH = Byte.MAX_VALUE;

  @Test
  public void searchByIndex() {
    String field = "file";
    String searchString = "file*";
    ObjectMapper keySerializer = new ObjectMapper();
    keySerializer.setVisibility(keySerializer.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

    IndexReader indexReader = null;
    try {
      StandardAnalyzer analyzer = new StandardAnalyzer();

      QueryParser parser = new QueryParser(field, analyzer);
      parser.setAllowLeadingWildcard(true);
      Query query = parser.parse(searchString);

      System.out.println(query.toString());
      File baseDir = new File(
          "H:/privat/git-sourcen/RestApplicationService/schematic/data/storage/SchematicApplication/wkla/SchematicApplication_lucene");
      MMapDirectory indexDirectory = new MMapDirectory(baseDir.toPath());

      indexReader = DirectoryReader.open(indexDirectory);
      IndexSearcher indexSearcher = new IndexSearcher(indexReader);

      ScoreDoc[] myhits = indexSearcher.search(query, 1000).scoreDocs;
      assertEquals(1, myhits.length);

      TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_SEARCH);
      indexSearcher.search(query, collector);

      TopDocs hits = collector.topDocs(0, MAX_SEARCH);

      Set<NitriteId> keySet = new LinkedHashSet<>();
      if (hits != null) {
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        if (scoreDocs != null) {
          for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            String jsonId = document.get(CONTENT_ID);
            NitriteId nitriteId = keySerializer.readValue(jsonId, NitriteId.class);
            keySet.add(nitriteId);
            System.out.println(nitriteId.toString());
          }
        }
      }
      assertTrue(keySet.size() > 0);

    } catch (IOException | ParseException e) {
      throw new IndexingException(errorMessage("could not search on full-text index", 0), e);
    } finally {
      try {
        if (indexReader != null)
          indexReader.close();
      } catch (IOException ignored) {
        // ignored
      }
    }
  }

  @Test
  public void testSimpleLucene() throws IOException, ParseException {
    Analyzer analyzer = new StandardAnalyzer();

    // Store the index in memory:
    File baseDir = new File("e:/temp/lucene");
    Files.remove(baseDir, true);
    baseDir.mkdirs();
    Directory directory = new MMapDirectory(baseDir.toPath());
    // Directory directory = new RAMDirectory();
    // To store an index on disk, use this instead:
    // Directory directory = FSDirectory.open("/tmp/testindex");
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    IndexWriter iwriter = new IndexWriter(directory, config);
    String text = "filename1";
    Document doc = new Document();
    doc.add(new StringField(CONTENT_ID, "{\"idValue\":776268696812874}", Field.Store.YES));
    doc.add(new Field("filename", text, TextField.TYPE_STORED));
    doc.add(new Field("schematicname", "schematicname1", TextField.TYPE_STORED));

    iwriter.addDocument(doc);

    doc = new Document();
    doc.add(new StringField(CONTENT_ID, "{\"idValue\":776268696812875}", Field.Store.YES));
    doc.add(new Field("filename", "filename2", TextField.TYPE_STORED));
    doc.add(new Field("schematicname", "schematicname2", TextField.TYPE_STORED));

    iwriter.addDocument(doc);

    doc = new Document();
    doc.add(new StringField(CONTENT_ID, "{\"idValue\":776268696812877}", Field.Store.YES));
    doc.add(new Field("filename", "filename3", TextField.TYPE_STORED));
    doc.add(new Field("schematicname", "schematicname3", TextField.TYPE_STORED));

    iwriter.addDocument(doc);

    iwriter.close();

    // Now search the index:
    DirectoryReader ireader = DirectoryReader.open(directory);
    IndexSearcher isearcher = new IndexSearcher(ireader);
    // Parse a simple query that searches for "text":
    QueryParser parser = new QueryParser("filename", analyzer);
    Query query = parser.parse("file*");
    ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
    assertEquals(1, hits.length);
    // Iterate through the results:
    for (int i = 0; i < hits.length; i++) {
      Document hitDoc = isearcher.doc(hits[i].doc);
      assertEquals("filename1", hitDoc.get("filename"));
    }
    ireader.close();
    directory.close();
  }
}
