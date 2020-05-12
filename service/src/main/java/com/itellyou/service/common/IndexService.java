package com.itellyou.service.common;

import com.itellyou.model.common.IndexModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public interface IndexService<T> {

    @Async
    void create(T detailModel);

    void create(IndexWriter indexWriter , T detailModel);

    @Async
    void delete(Long id);
    @Async
    void delete(HashSet<Long> ids);

    void delete(IndexWriter indexWriter , Long id);

    Long getId(T detailModel) throws NoSuchFieldException, IllegalAccessException, InstantiationException;

    @Async
    void update(T detailModel);

    @Async
    void update(List<T> models);

    IndexWriter getIndexWriter() throws InterruptedException;

    IndexReader getIndexReader() throws IOException;

    Document getDocument(Long id);

    Document getDocument(T detailModel);

    IndexModel getModel(Document document);

    @Async
    void createIndex(Long id);

    @Async
    void createIndex(T detailModel);

    @Async
    void updateIndex(Long id);

    @Async
    void updateIndex(HashSet<Long> ids);

    @Async
    void updateIndex(T detailModel);


}
