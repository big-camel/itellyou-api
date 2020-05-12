package com.itellyou.service.tag.impl;

import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagIndexModel;
import com.itellyou.service.common.impl.IndexServiceImpl;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class TagIndexServiceImpl extends IndexServiceImpl<TagDetailModel> {

    private final TagSearchService searchService;

    public TagIndexServiceImpl( TagSearchService searchService){
        super("./.indexer/tag");
        this.searchService = searchService;
    }

    public List<Document> getDocument(List<Long> ids) throws IOException {
        IndexReader indexReader = getIndexReader();
        IndexSearcher searcher = new IndexSearcher(indexReader);
        List<Document> list = new ArrayList<>();
        TopDocs docs = searcher.search(LongPoint.newSetQuery("id",ids),ids.size());
        if(docs != null && docs.scoreDocs.length > 0) {
            for (ScoreDoc doc : docs.scoreDocs){
                list.add(searcher.doc(doc.doc));
            }
        }
        return list;
    }

    @Override
    public TagIndexModel getModel(Document document) {
        TagIndexModel model = new TagIndexModel();
        model.setId(super.getModel(document).getId());
        String groupId = document.get("group_id");
        model.setGroupId(StringUtils.isNotEmpty(groupId) ? Long.parseLong(groupId) : 0);
        model.setName(document.get("name"));
        model.setContent(document.get("content"));
        String userId = document.get("created_user_id");
        model.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        return model;
    }

    @Override
    public Document getDocument(TagDetailModel detailModel) {
        Document doc = super.getDocument(detailModel);
        if(doc == null) return doc;

        String html = detailModel.getHtml();
        doc.add(new StoredField("type","tag"));
        doc.add(new LongPoint("group_id", detailModel.getGroupId()));
        doc.add(new StoredField("group_id",detailModel.getGroupId()));
        doc.add(new TextField("name", detailModel.getName(), Field.Store.YES));
        doc.add(new TextField("content", StringUtils.removeHtmlTags(html), Field.Store.YES));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new IntPoint("article_count",detailModel.getArticleCount()));
        doc.add(new IntPoint("question_count",detailModel.getQuestionCount()));
        doc.add(new LongPoint("created_time",detailModel.getCreatedTime()));
        doc.add(new LongPoint("updated_time",detailModel.getUpdatedTime()));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));
        doc.add(new StoredField("created_user_id",detailModel.getAuthor().getId()));
        double score = 1.5;
        score += detailModel.getStarCount() / 100.0;
        score += detailModel.getArticleCount() / 100.0;
        score += detailModel.getQuestionCount() / 100.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    @Async
    public void createIndex(Long id) {
        create(searchService.getDetail(id));
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        update(searchService.getDetail(id));
    }

    @Override
    @Async
    public void updateIndex(HashSet<Long> ids) {
        List<TagDetailModel> list = searchService.search(ids,null,null,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (TagDetailModel detailModel : list){
            update(detailModel);
        }
    }
}
