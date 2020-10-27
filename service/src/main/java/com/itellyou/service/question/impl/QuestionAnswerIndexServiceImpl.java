package com.itellyou.service.question.impl;

import com.itellyou.model.question.QuestionAnswerDetailModel;
import com.itellyou.model.question.QuestionAnswerIndexModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.service.question.QuestionAnswerSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
        doc.add(new IntPoint("comment_count",detailModel.getCommentCount()));
        doc.add(new IntPoint("view_count",detailModel.getViewCount()));
        doc.add(new IntPoint("support_count",detailModel.getSupportCount()));
        doc.add(new IntPoint("oppose_count",detailModel.getOpposeCount()));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new LongPoint("created_time", DateUtils.getTimestamp(detailModel.getCreatedTime(),0l)));
        doc.add(new LongPoint("updated_time",DateUtils.getTimestamp(detailModel.getUpdatedTime(),0l)));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));
        double score = 1.0;
        if(detailModel.isAdopted()) score += 0.1;
        score += detailModel.getCommentCount() / 100.0;
        score += detailModel.getViewCount() / 1000.0;
        score += detailModel.getSupportCount() / 500.0;
        score -= detailModel.getOpposeCount() / 500.0;
        score += detailModel.getStarCount() / 1000.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public QuestionAnswerIndexModel getModel(Document document) {
        return new QuestionAnswerIndexModel(document);
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
    public void updateIndex(Collection<Long> ids) {
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
