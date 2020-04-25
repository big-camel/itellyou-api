package com.itellyou.service.article;

import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleIndexModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Map;

public interface ArticleIndexService {
    @Async
    void create(ArticleDetailModel detailModel);

    void create(IndexWriter indexWriter ,ArticleDetailModel detailModel);

    @Async
    void delete(Long id);

    void delete(IndexWriter indexWriter , Long id);

    @Async
    void update(ArticleDetailModel detailModel);

    IndexWriter getIndexWriter() throws InterruptedException;

    IndexReader getIndexReader() throws IOException;

    Document getDocument(Long id) throws IOException;

    Document getDocument(ArticleDetailModel detailModel);

    ArticleIndexModel getModel(Document document);

    @Async
    void createIndex(Long id);

    @Async
    void createIndex(ArticleDetailModel detailModel);

    @Async
    void updateIndex(Long id);

    @Async
    void updateIndex(ArticleDetailModel detailModel);
}
