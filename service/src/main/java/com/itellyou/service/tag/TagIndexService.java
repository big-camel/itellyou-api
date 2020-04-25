package com.itellyou.service.tag;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagIndexModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface TagIndexService {
    @Async
    void create(TagDetailModel detailModel);

    void create(IndexWriter indexWriter , TagDetailModel detailModel);

    @Async
    void delete(Long id);

    void delete(IndexWriter indexWriter , Long id);

    @Async
    void update(TagDetailModel detailModel);

    IndexWriter getIndexWriter() throws InterruptedException;

    Document getDocument(TagDetailModel detailModel);

    IndexReader getIndexReader() throws IOException;

    Document getDocument(Long id) throws IOException;

    List<Document> getDocument(List<Long> id) throws IOException;

    TagIndexModel getModel(Document document);
    @Async
    void createIndex(Long id);

    @Async
    void createIndex(TagDetailModel model);

    @Async
    void updateIndex(Long id);

    @Async
    void updateIndex(TagDetailModel model);

    @Async
    void updateIndex(HashSet<Long> ids);

}
