package com.itellyou.service.user.impl;

import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserIndexModel;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserIndexServiceImpl extends IndexServiceImpl<UserDetailModel> {

    private final UserSearchService searchService;
    public UserIndexServiceImpl(UserSearchService searchService) {
        super("./.indexer/user");
        this.searchService = searchService;
    }

    @Override
    public Document getDocument(UserDetailModel infoModel) {
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
        doc.add(new TextField("description", StringUtils.isNotEmpty(infoModel.getDescription()) ? "" : infoModel.getDescription(), Field.Store.YES));
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
    @Async
    public void createIndex(Long id) {
        create(searchService.find(id,null));
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        update(searchService.find(id,null));
    }


    @Override
    @Async
    public void updateIndex(HashSet<Long> ids) {
        update(searchService.search(ids,null,null,null,null
                ,null,null,null,null,null,null,null));
    }
}
