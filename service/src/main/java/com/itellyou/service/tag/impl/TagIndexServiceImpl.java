package com.itellyou.service.tag.impl;

import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagIndexModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.tag.TagIndexService;
import com.itellyou.service.tag.TagSearchService;
import com.itellyou.util.StringUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class TagIndexServiceImpl implements TagIndexService {
    private final String direct = "./.indexer/tag";

    private final IndexService indexService;

    private final TagSearchService searchService;

    public TagIndexServiceImpl(IndexService indexService, TagSearchService searchService){
        this.indexService = indexService;
        this.searchService = searchService;
    }

    @Override
    @Async
    public void create(TagDetailModel detailModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            create(indexWriter,detailModel);
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void create(IndexWriter indexWriter, TagDetailModel detailModel) {
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
    public void update(TagDetailModel detailModel) {
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
        String id = document.get("id");
        model.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : 0);
        String groupId = document.get("group_id");
        model.setGroupId(StringUtils.isNotEmpty(groupId) ? Long.parseLong(groupId) : 0);
        model.setName(document.get("name"));
        model.setContent(document.get("content"));
        String userId = document.get("created_user_id");
        model.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        return model;
    }


    @Override
    public IndexWriter getIndexWriter() {
        return indexService.getIndexWriter(direct);
    }

    @Override
    public Document getDocument(TagDetailModel detailModel) {
        // 创建一个存储对象
        Document doc = new Document();

        String html = detailModel.getHtml();
        Long id = detailModel.getId();
        if(id == null) return null;
        doc.add(new StoredField("type","tag"));
        doc.add(new LongPoint("id", id));
        doc.add(new StoredField("id",id));
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
    public void createIndex(TagDetailModel model) {
        create(model);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        update(searchService.getDetail(id));
    }

    @Override
    @Async
    public void updateIndex(TagDetailModel model) {
    update(model);
    }

    @Override
    public void updateIndex(HashSet<Long> ids) {
        List<TagDetailModel> list = searchService.search(ids,null,null,null,null,null,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (TagDetailModel detailModel : list){
            update(detailModel);
        }
    }
}
