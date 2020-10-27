package com.itellyou.service.article.impl;

import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleIndexModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class ArticleIndexServiceImpl extends IndexServiceImpl<ArticleDetailModel> {

    private final ArticleSearchService searchService;

    public ArticleIndexServiceImpl(ArticleSearchService searchService){
        super("./.indexer/article");
        this.searchService = searchService;
    }

    @Override
    public Document getDocument(ArticleDetailModel detailModel) {

        Document doc = super.getDocument(detailModel);
        if(doc == null) return doc;

        String html = detailModel.getHtml();
        doc.add(new StoredField("type","article"));
        doc.add(new TextField("title", detailModel.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", StringUtils.removeHtmlTags(html), Field.Store.YES));
        doc.add(new IntPoint("source_type",detailModel.getSourceType().getValue()));
        doc.add(new StringField("source_data",detailModel.getSourceData(),Field.Store.NO));
        List<TagDetailModel> tagList = detailModel.getTags();
        for (TagDetailModel model : tagList){
            doc.add(new LongPoint("tags",model.getId()));
        }
        doc.add(new IntPoint("comment_count",detailModel.getCommentCount()));
        doc.add(new IntPoint("view_count",detailModel.getViewCount()));
        doc.add(new IntPoint("support_count",detailModel.getSupportCount()));
        doc.add(new IntPoint("oppose_count",detailModel.getOpposeCount()));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new LongPoint("created_time",DateUtils.getTimestamp(detailModel.getCreatedTime(),0l)));
        doc.add(new LongPoint("updated_time", DateUtils.getTimestamp(detailModel.getUpdatedTime(),0l)));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));
        ColumnInfoModel columnModel = detailModel.getColumn();
        Long columnId = columnModel != null ? columnModel.getId() : 0l;
        doc.add(new LongPoint("column_id",columnId));
        doc.add(new StoredField("column_id",columnId));
        double score = 1.0;
        score += detailModel.getCommentCount() / 50.0;
        score += detailModel.getViewCount() / 1000.0;
        score += detailModel.getSupportCount() / 500.0;
        score -= detailModel.getOpposeCount() / 500.0;
        score += detailModel.getStarCount() / 100.0;
        if(columnModel != null){
            score += 0.05;
            score += columnModel.getStarCount() / 100;
        }
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public ArticleIndexModel getModel(Document document) {
        return new ArticleIndexModel(document);
    }

    @Override
    @Async
    public void createIndex(Long id) {
        ArticleDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled()) {
            delete(id);
            return;
        }
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        ArticleDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled()) {
            delete(id);
            return;
        }
        update(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Collection<Long> ids) {
        List<ArticleDetailModel> list = searchService.search(ids,null,null,null,null
                ,null,true,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null,null
                ,null,null,null,null);
        List<ArticleDetailModel> updateModels = new LinkedList<>();
        for (ArticleDetailModel articleModel : list) {
            if(articleModel.isDeleted() || articleModel.isDisabled()) {
                delete(articleModel.getId());
            }else
            {
                updateModels.add(articleModel);
            }
        }
        if(updateModels.size() > 0)
            update(updateModels);
    }
}
