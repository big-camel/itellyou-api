package com.itellyou.service.column;

import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnIndexModel;
import com.itellyou.model.column.ColumnInfoModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Map;

public interface ColumnIndexService {
    @Async
    void create(ColumnDetailModel infoModel);

    void create(IndexWriter indexWriter , ColumnDetailModel detailModel);

    @Async
    void delete(Long id);

    void delete(IndexWriter indexWriter , Long id);

    @Async
    void update(ColumnDetailModel infoModel);

    IndexWriter getIndexWriter() throws InterruptedException;

    IndexReader getIndexReader() throws IOException;

    Document getDocument(Long id) throws IOException;

    Document getDocument(ColumnDetailModel infoModel);

    ColumnIndexModel getModel(Document document);

    @Async
    void createIndex(Long id);

    @Async
    void createIndex(ColumnDetailModel model);

    @Async
    void updateIndex(Long id);

    @Async
    void updateIndex(ColumnDetailModel model);
}
