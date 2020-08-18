package com.itellyou.service.question.impl;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerIndexModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
public class QuestionAnswerIndexServiceImpl extends IndexServiceImpl<QuestionAnswerDetailModel> {

    private final IndexService<QuestionDetailModel> questionIndexService;
    private final QuestionAnswerSearchService answerSearchService;

    @Autowired
    public QuestionAnswerIndexServiceImpl(QuestionIndexServiceImpl questionIndexService, QuestionAnswerSearchService answerSearchService){
        super("./.indexer/answer");
        this.questionIndexService = questionIndexService;
        this.answerSearchService = answerSearchService;
    }

    @Override
    public Document getDocument(QuestionAnswerDetailModel detailModel) {
        Document questionDocument = questionIndexService.getDocument(detailModel.getQuestionId());

        Document doc = super.getDocument(detailModel);
        if(doc == null) return doc;
        String html = detailModel.getHtml();
        doc.add(new StoredField("type","answer"));
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
        model.setId(super.getModel(document).getId());
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
        QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled() || !detailModel.isPublished()) {
            delete(id);
            return;
        }
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        QuestionAnswerDetailModel detailModel = answerSearchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled() || !detailModel.isPublished()) {
            delete(id);
            return;
        }
        update(detailModel);
    }

    @Override
    @Async
    public void updateIndex(HashSet<Long> ids) {
        List<QuestionAnswerDetailModel> list = answerSearchService.search(ids,null,null,null,null,true,null,null,true,null,null,null,null,null,null);
        List<QuestionAnswerDetailModel> updateModels = new LinkedList<>();
        for (QuestionAnswerDetailModel model : list) {
            if(model.isDeleted() || model.isDisabled()) {
                delete(model.getId());
            }else
            {
                updateModels.add(model);
            }
        }
        if(updateModels.size() > 0)
            update(updateModels);
    }
}
