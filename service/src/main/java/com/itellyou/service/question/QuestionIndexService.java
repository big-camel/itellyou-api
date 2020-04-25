package com.itellyou.service.question;

import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionIndexModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Map;

public interface QuestionIndexService {
    @Async
    void create(QuestionDetailModel detailModel);

    void create(IndexWriter indexWriter , QuestionDetailModel detailModel);

    @Async
    void delete(Long id);

    void delete(IndexWriter indexWriter , Long id);

    @Async
    void update(QuestionDetailModel detailModel);

    IndexWriter getIndexWriter();

    IndexReader getIndexReader();

    Document getDocument(Long id) throws IOException;

    Document getDocument(QuestionDetailModel detailModel);

    QuestionIndexModel getModel(Document document);

    @Async
    void createIndex(Long id);

    @Async
    void createIndex(QuestionDetailModel detailModel);

    @Async
    void updateIndex(Long id);

    @Async
    void updateIndex(QuestionDetailModel detailModel);
}
