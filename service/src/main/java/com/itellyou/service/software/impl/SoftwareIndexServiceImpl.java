package com.itellyou.service.software.impl;

import com.itellyou.model.software.SoftwareDetailModel;
import com.itellyou.model.software.SoftwareGroupModel;
import com.itellyou.model.software.SoftwareIndexModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.service.software.SoftwareSearchService;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class SoftwareIndexServiceImpl extends IndexServiceImpl<SoftwareDetailModel> {

    private final SoftwareSearchService searchService;

    public SoftwareIndexServiceImpl(SoftwareSearchService searchService){
        super("./.indexer/software");
        this.searchService = searchService;
    }

    @Override
    public Document getDocument(SoftwareDetailModel detailModel) {

        Document doc = super.getDocument(detailModel);
        if(doc == null) return doc;

        String html = detailModel.getHtml();
        doc.add(new StoredField("type","software"));
        doc.add(new TextField("name", detailModel.getName(), Field.Store.YES));
        doc.add(new TextField("content", StringUtils.removeHtmlTags(html), Field.Store.YES));
        List<TagDetailModel> tagList = detailModel.getTags();
        for (TagDetailModel model : tagList){
            doc.add(new LongPoint("tags",model.getId()));
        }
        doc.add(new IntPoint("comment_count",detailModel.getCommentCount()));
        doc.add(new IntPoint("view",detailModel.getView()));
        doc.add(new IntPoint("support",detailModel.getSupport()));
        doc.add(new IntPoint("oppose",detailModel.getOppose()));
        doc.add(new LongPoint("created_time",detailModel.getCreatedTime()));
        doc.add(new LongPoint("updated_time",detailModel.getUpdatedTime()));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));
        SoftwareGroupModel groupModel = detailModel.getGroup();
        Long groupId = groupModel != null ? groupModel.getId() : 0l;
        doc.add(new LongPoint("group_id",groupId));
        doc.add(new StoredField("group_id",groupId));
        double score = 1.0;
        score += detailModel.getCommentCount() / 50.0;
        score += detailModel.getView() / 1000.0;
        score += detailModel.getSupport() / 500.0;
        score -= detailModel.getOppose() / 500.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public SoftwareIndexModel getModel(Document document) {
        SoftwareIndexModel model =  new SoftwareIndexModel();
        model.setId(super.getModel(document).getId());
        model.setName(document.get("name"));
        model.setContent(document.get("content"));
        String userId = document.get("created_user_id");
        model.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        String groupId = document.get("group_id");
        model.setGroupId(StringUtils.isNotEmpty(groupId) ? Long.parseLong(groupId) : 0);
        return model;
    }

    @Override
    @Async
    public void createIndex(Long id) {
        SoftwareDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled()) {
            delete(id);
            return;
        }
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        SoftwareDetailModel detailModel = searchService.getDetail(id);
        if(detailModel.isDeleted() || detailModel.isDisabled()) {
            delete(id);
            return;
        }
        update(detailModel);
    }

    @Override
    @Async
    public void updateIndex(HashSet<Long> ids) {
        update(searchService.search(ids,null,null,null,null
        ,true,false,false,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null));
    }
}
