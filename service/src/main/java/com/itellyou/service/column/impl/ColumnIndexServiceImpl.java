package com.itellyou.service.column.impl;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnIndexModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.column.ColumnSearchService;
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
public class ColumnIndexServiceImpl extends IndexServiceImpl<ColumnDetailModel> {

    private final ColumnSearchService searchService;

    public ColumnIndexServiceImpl(ColumnSearchService searchService){
        super("./.indexer/column");
        this.searchService = searchService;
    }

    @Override
    public Document getDocument(ColumnDetailModel detailModel) {
        Document doc = super.getDocument(detailModel);
        if(doc == null) return doc;

        doc.add(new StoredField("type","column"));
        doc.add(new TextField("name", detailModel.getName(), Field.Store.YES));
        doc.add(new TextField("description", StringUtils.removeHtmlTags(detailModel.getDescription()), Field.Store.YES));
        List<TagDetailModel> tagList = detailModel.getTags();
        for (TagInfoModel model : tagList){
            doc.add(new LongPoint("tags",model.getId()));
        }
        doc.add(new IntPoint("article_count",detailModel.getArticleCount()));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new LongPoint("created_time", DateUtils.getTimestamp(detailModel.getCreatedTime(),0l)));
        doc.add(new LongPoint("updated_time",DateUtils.getTimestamp(detailModel.getUpdatedTime(),0l)));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));

        double score = 1.0;
        score += detailModel.getArticleCount() / 100.0;
        score += detailModel.getStarCount() / 100.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public ColumnIndexModel getModel(Document document) {
        return new ColumnIndexModel(document);
    }

    @Override
    @Async
    public void createIndex(Long id) {
        ColumnDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled()) {
            delete(id);
            return;
        }
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        ColumnDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled()) {
            delete(id);
            return;
        }
        update(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Collection<Long> ids) {
        List<ColumnDetailModel> list = searchService.search(ids,null,null,null,null
                ,null,true,null,null,null,null,null,null,null,null,null,null,null,null);
        List<ColumnDetailModel> updateModels = new LinkedList<>();
        for (ColumnDetailModel model : list) {
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
