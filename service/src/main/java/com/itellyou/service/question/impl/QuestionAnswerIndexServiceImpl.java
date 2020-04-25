package com.itellyou.service.question.impl;

import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerIndexModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.question.QuestionAnswerIndexService;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.service.question.QuestionIndexService;
import com.itellyou.util.StringUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class QuestionAnswerIndexServiceImpl implements QuestionAnswerIndexService {

    private final String direct = "./.indexer/answer";

    private final IndexService indexService;

    private final QuestionIndexService questionIndexService;
    private final QuestionAnswerSearchService answerSearchService;

    @Autowired
    public QuestionAnswerIndexServiceImpl(IndexService indexService, QuestionIndexService questionIndexService, QuestionAnswerSearchService answerSearchService){
        this.indexService = indexService;
        this.questionIndexService = questionIndexService;
        this.answerSearchService = answerSearchService;
    }

    @Override
    @Async
    public void create(QuestionAnswerDetailModel detailModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            create(indexWriter,detailModel);
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void create(IndexWriter indexWriter, QuestionAnswerDetailModel detailModel) {
        try {
            indexWriter.addDocument(getDocument(detailModel));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void delete(Long id) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            indexWriter.deleteDocuments(LongPoint.newExactQuery("id",id));
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(IndexWriter indexWriter, Long id) {
        try {
            indexWriter.deleteDocuments(LongPoint.newExactQuery("id",id));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void update(QuestionAnswerDetailModel detailModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            if(detailModel.getId() != null) delete(indexWriter,detailModel.getId());
            create(indexWriter,detailModel);
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IndexWriter getIndexWriter() {
        return indexService.getIndexWriter(direct);
    }

    @Override
    public IndexReader getIndexReader(){
        return indexService.getIndexReader(direct);
    }

    @Override
    public Document getDocument(Long id) throws IOException {
        IndexReader indexReader = getIndexReader();
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs docs = searcher.search(LongPoint.newExactQuery("id",id),1);
        if(docs != null && docs.scoreDocs.length > 0) return searcher.doc(docs.scoreDocs[0].doc);
        return null;
    }

    @Override
    public Document getDocument(QuestionAnswerDetailModel detailModel) throws IOException {
        Document questionDocument = questionIndexService.getDocument(detailModel.getQuestionId());

        Document doc = new Document();
        String html = detailModel.getHtml();
        Long id = detailModel.getId();
        if(id == null) return null;
        doc.add(new StoredField("type","answer"));
        doc.add(new LongPoint("id", id));
        doc.add(new StoredField("id",id));
        doc.add(new LongPoint("question_id", detailModel.getQuestionId()));
        doc.add(new StoredField("question_id",detailModel.getQuestionId()));
        doc.add(new TextField("title", questionDocument == null ? "" : questionDocument.get("title"), Field.Store.YES));
        doc.add(new TextField("content", StringUtils.removeHtmlTags(html), Field.Store.YES));
        doc.add(new IntPoint("comments",detailModel.getComments()));
        doc.add(new IntPoint("view",detailModel.getView()));
        doc.add(new IntPoint("support",detailModel.getSupport()));
        doc.add(new IntPoint("oppose",detailModel.getOppose()));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new LongPoint("created_time",detailModel.getCreatedTime()));
        doc.add(new LongPoint("updated_time",detailModel.getUpdatedTime()));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));
        double score = 1.0;
        if(detailModel.isAdopted()) score += 0.1;
        score += detailModel.getComments() / 100.0;
        score += detailModel.getView() / 1000.0;
        score += detailModel.getSupport() / 500.0;
        score -= detailModel.getOppose() / 500.0;
        score += detailModel.getStarCount() / 1000.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public QuestionAnswerIndexModel getModel(Document document) {
        QuestionAnswerIndexModel model = new QuestionAnswerIndexModel();
        String id = document.get("id");
        model.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : 0);
        model.setTitle(document.get("title"));
        model.setContent(document.get("content"));
        String userId = document.get("created_user_id");
        model.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        String questionId = document.get("question_id");
        model.setQuestionId(StringUtils.isNotEmpty(questionId) ? Long.parseLong(questionId) : 0);
        return model;
    }


    @Override
    @Async
    public void createIndex(Long id) {
        create(answerSearchService.getDetail(id));
    }

    @Override
    @Async
    public void createIndex(QuestionAnswerDetailModel detailModel) {
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        update(answerSearchService.getDetail(id));
    }

    @Override
    @Async
    public void updateIndex(QuestionAnswerDetailModel detailModel) {
        update(detailModel);
    }
}
