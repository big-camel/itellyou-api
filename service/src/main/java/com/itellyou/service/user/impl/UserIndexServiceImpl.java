package com.itellyou.service.user.impl;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.user.UserIndexModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.user.UserIndexService;
import com.itellyou.util.StringUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class UserIndexServiceImpl implements UserIndexService {

    private final String direct = "./.indexer/user";

    private final IndexService indexService;

    public UserIndexServiceImpl(IndexService indexService) {
        this.indexService = indexService;
    }

    @Override
    @Async
    public void create(UserInfoModel infoModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            create(indexWriter,infoModel);
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void create(IndexWriter indexWriter, UserInfoModel detailModel) {
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
    public void delete(IndexWriter indexWriter , Long id) {
        try {
            indexWriter.deleteDocuments(LongPoint.newExactQuery("id",id));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void update(UserInfoModel infoModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            if(infoModel.getId() != null) delete(indexWriter,infoModel.getId());
            create(indexWriter,infoModel);
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
    public Document getDocument(UserInfoModel infoModel) {
        Document doc = new Document();
        doc.add(new StoredField("type","user"));
        doc.add(new LongPoint("id", infoModel.getId()));
        doc.add(new StoredField("id",infoModel.getId()));
        doc.add(new IntPoint("article_count",infoModel.getArticleCount()));
        doc.add(new IntPoint("star_count",infoModel.getStarCount()));
        doc.add(new IntPoint("follower_count",infoModel.getFollowerCount()));
        doc.add(new IntPoint("column_count",infoModel.getColumnCount()));
        doc.add(new IntPoint("question_count",infoModel.getQuestionCount()));
        doc.add(new IntPoint("answer_count",infoModel.getAnswerCount()));
        doc.add(new TextField("name", infoModel.getName(), Field.Store.YES));
        doc.add(new TextField("description", infoModel.getDescription(), Field.Store.YES));
        double score = 1.0;
        score += infoModel.getFollowerCount() / 10.0;
        score += infoModel.getAnswerCount() / 20.0;
        score += infoModel.getArticleCount() / 30.0;
        score += infoModel.getColumnCount() / 20.0;
        score += infoModel.getQuestionCount() / 40.0;
        score += infoModel.getStarCount() / 100.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public UserIndexModel getModel(Document document) {
        UserIndexModel model = new UserIndexModel();
        String id = document.get("id");
        model.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : 0);
        model.setName(document.get("name"));
        model.setDescription(document.get("description"));
        return model;
    }

    @Override
    public IndexReader getIndexReader(){
        return indexService.getIndexReader(direct);
    }
}
