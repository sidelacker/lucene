package com.sidelacker;

import com.sidelacker.model.Result;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
public class App {

    private StandardAnalyzer analyzer;
    private Directory memoryIndex;

    @PostConstruct
    public void setup() {
        try {
            memoryIndex = new RAMDirectory();
            analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
            Document document1 = new Document();

            document1.add(new TextField("title", "Catcher", Field.Store.YES));
            document1.add(new TextField("body", "Some story 1", Field.Store.YES));

            writter.addDocument(document1);

            Document document2 = new Document();

            document2.add(new TextField("title", "Cat In the Hat", Field.Store.YES));
            document2.add(new TextField("body", "Some story 2", Field.Store.YES));

            writter.addDocument(document2);

            Document document3 = new Document();

            document3.add(new TextField("title", "Hat times", Field.Store.YES));
            document3.add(new TextField("body", "Some story 3", Field.Store.YES));

            writter.addDocument(document3);

            writter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/search/{term}")
    public List<Result> index(@PathVariable String term) throws Exception {


        Query query = new QueryParser("title", analyzer)
                .parse(term);

        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        List<Result> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            results.add(new Result(searcher.doc(scoreDoc.doc).get("title") + " - " + searcher.doc(scoreDoc.doc).get("body")));
        }

        return results;

    }




}