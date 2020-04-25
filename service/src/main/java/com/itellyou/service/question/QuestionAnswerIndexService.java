package com.itellyou.service.question;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerIndexModel;
import com.itellyou.model.question.QuestionDetailModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Map;

public interface QuestionAnswerIndexService {
    @Async
    void create(QuestionAnswerDetailModel detailModel);

    void create(IndexWriter indexWriter, QuestionAnswerDetailModel detailModel);

    @Async
    void delete(Long id);

    void delete(IndexWriter indexWriter, Long id);

    @Async
    void update(QuestionAnswerDetailModel detailModel);

    IndexWriter getIndexWriter();

    IndexReader getIndexReader();

    Document getDocument(Long id) throws IOException;

    Document getDocument(QuestionAnswerDetailModel detailModel) throws IOException;

    QuestionAnswerIndexModel getModel(Document document);

    @Async
    void createIndex(Long id);

    @Async
    void createIndex(QuestionAnswerDetailModel detailModel);

    @Async
    void updateIndex(Long id);

    @Async
    void updateIndex(QuestionAnswerDetailModel detailModel);
}
