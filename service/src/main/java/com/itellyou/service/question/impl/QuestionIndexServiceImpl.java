package com.itellyou.service.question.impl;

import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionIndexModel;
import com.itellyou.model.sys.RewardType;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class QuestionIndexServiceImpl extends IndexServiceImpl<QuestionDetailModel> {
    private final QuestionSearchService searchService;

    public QuestionIndexServiceImpl(QuestionSearchService searchService){
        super("./.indexer/question");
        this.searchService = searchService;
    }

    @Override
    public Document getDocument(QuestionDetailModel detailModel) {

        Document doc = super.getDocument(detailModel);
        if(doc == null) return doc;

        String html = detailModel.getHtml();
        doc.add(new StoredField("type","question"));
        doc.add(new TextField("title", detailModel.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", StringUtils.removeHtmlTags(html), Field.Store.YES));
        doc.add(new IntPoint("reward_type",detailModel.getRewardType().getValue()));
        doc.add(new DoublePoint("reward_value",detailModel.getRewardValue()));
        List<TagDetailModel> tagList = detailModel.getTags();
        for (TagDetailModel model : tagList){
            doc.add(new LongPoint("tags",model.getId()));
        }
        doc.add(new IntPoint("answer_count",detailModel.getAnswerCount()));
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
        if(detailModel.getRewardType() == RewardType.CREDIT){
            score += detailModel.getRewardValue() / 100.0;
        }
        if(detailModel.getRewardType() == RewardType.CASH){
            score += detailModel.getRewardValue() / 10.0;
        }
        score += detailModel.getAnswerCount() / 10.0;
        score += detailModel.getCommentCount() / 20.0;
        score += detailModel.getViewCount() / 1000.0;
        score += detailModel.getSupportCount() / 500.0;
        score -= detailModel.getOpposeCount() / 500.0;
        score += detailModel.getStarCount() / 1000.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public QuestionIndexModel getModel(Document document) {
        return new QuestionIndexModel(document);
    }

    @Override
    @Async
    public void createIndex(Long id) {
        QuestionDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled() || !detailModel.isPublished()) {
            delete(id);
            return;
        }
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        QuestionDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled() || !detailModel.isPublished()) {
            delete(id);
            return;
        }
        update(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Collection<Long> ids) {
        List<QuestionDetailModel> list = searchService.search(ids,null,null,null,true,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        List<QuestionDetailModel> updateModels = new LinkedList<>();
        for (QuestionDetailModel model : list) {
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
