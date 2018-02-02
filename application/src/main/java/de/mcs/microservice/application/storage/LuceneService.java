/*
 *
 * Copyright 2017 Nitrite author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.mcs.microservice.application.storage;

import static org.dizitart.no2.exceptions.ErrorMessage.errorMessage;
import static org.dizitart.no2.util.StringUtils.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.exceptions.IndexingException;
import org.dizitart.no2.fulltext.TextIndexingService;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.Monitor;

public class LuceneService implements TextIndexingService {
  private static final String CONTENT_ID = "content_id";
  private static final int MAX_SEARCH = Byte.MAX_VALUE;

  private IndexWriter indexWriter;
  private ObjectMapper keySerializer;
  private Analyzer analyzer;
  private Directory indexDirectory;
  private File baseDir;
  private ScheduledExecutorService scheduledExecutor;
  private ScheduledFuture<?> commitFuture;
  private SearcherManager searcherManager;
  private ScheduledFuture<?> maybeRefreshFuture;
  private ScheduledFuture<?> compactFuture;

  public LuceneService(File baseDir) {
    Monitor monitor = MeasureFactory.start(this, "newIndexService");
    this.baseDir = baseDir;
    try {
      this.keySerializer = new ObjectMapper();
      keySerializer.setVisibility(keySerializer.getSerializationConfig().getDefaultVisibilityChecker()
          .withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
          .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

      indexDirectory = new MMapDirectory(baseDir.toPath());
      analyzer = new StandardAnalyzer();

      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
      indexWriter = new IndexWriter(indexDirectory, iwc);
      scheduledExecutor = Executors.newScheduledThreadPool(3);

      commitFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
        try {
          if (indexWriter != null) {
            System.out.println("lucene commit.");
            indexWriter.commit();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }, 10, 10, TimeUnit.SECONDS);

      compactFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
        try {
          if (indexWriter != null) {
            System.out.println("lucene maybeMerge.");
            indexWriter.maybeMerge();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }, 1, 1, TimeUnit.MINUTES);

      searcherManager = new SearcherManager(indexWriter, true, true, null);
      maybeRefreshFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
        try {
          System.out.println("lucene maybeRefresh.");
          searcherManager.maybeRefresh();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }, 0, 5, TimeUnit.SECONDS);
    } catch (IOException e) {
      throw new IndexingException(errorMessage("could not create full-text index", 0), e);
    } catch (VirtualMachineError vme) {
      handleVirtualMachineError(vme);
    } finally {
      monitor.stop();
    }
  }

  @Override
  public void createIndex(NitriteId id, String field, String text) {
    Monitor monitor = MeasureFactory.start(this, "createIndex");
    try {
      Document document = new Document();
      String jsonId = keySerializer.writeValueAsString(id);
      Field contentField = new TextField(field, text, Field.Store.NO);
      Field idField = new StringField(CONTENT_ID, jsonId, Field.Store.YES);

      document.add(idField);
      document.add(contentField);

      indexWriter.addDocument(document);
    } catch (IOException ioe) {
      throw new IndexingException(errorMessage("could not write full-text index data for " + text, 0), ioe);
    } catch (VirtualMachineError vme) {
      handleVirtualMachineError(vme);
    } finally {
      monitor.stop();
    }
  }

  @Override
  public void updateIndex(NitriteId id, String field, String text) {
    Monitor monitor = MeasureFactory.start(this, "updateIndex");
    try {
      String jsonId = keySerializer.writeValueAsString(id);
      Document document = getDocument(jsonId);
      if (document == null) {
        document = new Document();
        Field idField = new StringField(CONTENT_ID, jsonId, Field.Store.YES);
        document.add(idField);
      }
      Field contentField = new TextField(field, text, Field.Store.YES);

      document.add(contentField);

      Monitor update = MeasureFactory.start(this, "updateDocument");
      try {
        indexWriter.updateDocument(new Term(CONTENT_ID, jsonId), document);
      } finally {
        update.stop();
      }
    } catch (IOException ioe) {
      throw new IndexingException(errorMessage("could not update full-text index for " + text, 0), ioe);
    } catch (VirtualMachineError vme) {
      handleVirtualMachineError(vme);
    } finally {
      monitor.stop();
    }
  }

  @Override
  public void deleteIndex(NitriteId id, String field, String text) {
    Monitor monitor = MeasureFactory.start(this, "deleteIndex");
    try {
      String jsonId = keySerializer.writeValueAsString(id);
      Term idTerm = new Term(CONTENT_ID, jsonId);

      indexWriter.deleteDocuments(idTerm);
    } catch (IOException ioe) {
      throw new IndexingException(errorMessage("could not remove full-text index for " + id, 0));
    } catch (VirtualMachineError vme) {
      handleVirtualMachineError(vme);
    } finally {
      monitor.stop();
    }
  }

  @Override
  public void deleteIndexesByField(String field) {
    if (!isNullOrEmpty(field)) {
      try {
        Query query;
        QueryParser parser = new QueryParser(field, analyzer);
        parser.setAllowLeadingWildcard(true);
        try {
          query = parser.parse("*");
        } catch (ParseException e) {
          throw new IndexingException(errorMessage("could not remove full-text index for value " + field, 0));
        }

        indexWriter.deleteDocuments(query);
      } catch (IOException ioe) {
        throw new IndexingException(errorMessage("could not remove full-text index for value " + field, 0));
      } catch (VirtualMachineError vme) {
        handleVirtualMachineError(vme);
      }
    }
  }

  @Override
  public Set<NitriteId> searchByIndex(String field, String searchString) {
    Monitor monitor = MeasureFactory.start(this, "searchByIndex");
    try {
      QueryParser parser = new QueryParser(field, analyzer);
      parser.setAllowLeadingWildcard(true);
      Query query = parser.parse(searchString + "*");

      searcherManager.maybeRefreshBlocking();
      IndexSearcher indexSearcher = searcherManager.acquire();
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
          }
        }
      }

      return keySet;
    } catch (IOException | ParseException e) {
      throw new IndexingException(errorMessage("could not search on full-text index", 0), e);
    } finally {
      monitor.stop();
    }
  }

  private Document getDocument(String jsonId) {
    Monitor monitor = MeasureFactory.start(this, "getDocument");
    try {
      Term idTerm = new Term(CONTENT_ID, jsonId);

      TermQuery query = new TermQuery(idTerm);

      IndexSearcher indexSearcher = searcherManager.acquire();

      TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_SEARCH);
      indexSearcher.search(query, collector);

      TopDocs hits = collector.topDocs(0, MAX_SEARCH);
      Document document = null;
      if (hits != null) {
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        if (scoreDocs != null) {
          for (ScoreDoc scoreDoc : scoreDocs) {
            document = indexSearcher.doc(scoreDoc.doc);
          }
        }
      }

      return document;
    } catch (IOException e) {
      throw new IndexingException(errorMessage("could not search on full-text index", 0), e);
    } finally {
      monitor.stop();
    }
  }

  @Override
  public void drop() {
    try {
      indexDirectory = new MMapDirectory(baseDir.toPath());
      analyzer = new StandardAnalyzer();

      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
      indexWriter = new IndexWriter(indexDirectory, iwc);
    } catch (IOException e) {
      throw new IndexingException(errorMessage("could not drop full-text index", 0), e);
    }
  }

  @Override
  public void clear() {
    Monitor monitor = MeasureFactory.start(this, "clear");
    try {
      indexWriter.deleteAll();
    } catch (IOException e) {
      throw new IndexingException(errorMessage("could not clear full-text index", 0), e);
    } finally {
      monitor.stop();
    }
  }

  private void handleVirtualMachineError(VirtualMachineError vme) {
    if (indexWriter != null) {
      try {
        indexWriter.close();
      } catch (IOException ioe) {
        // ignore it
      }
    }
    scheduledExecutor.shutdown();
    throw vme;
  }
}
