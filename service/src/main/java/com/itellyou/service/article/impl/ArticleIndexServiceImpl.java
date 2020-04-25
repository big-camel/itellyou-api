package com.itellyou.service.article.impl;

import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleIndexModel;
import com.itellyou.model.column.ColumnInfoModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.article.ArticleIndexService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.common.IndexService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleIndexServiceImpl implements ArticleIndexService {
    private final String direct = "./.indexer/article";

    private final IndexService indexService;
    private final ArticleSearchService searchService;

    @Autowired
    public ArticleIndexServiceImpl(IndexService indexService,ArticleSearchService searchService){
        this.indexService = indexService;
        this.searchService = searchService;
    }

    @Override
    @Async
    public void create(ArticleDetailModel detailModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            indexWriter.addDocument(getDocument(detailModel));
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void create(IndexWriter indexWriter, ArticleDetailModel detailModel) {
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
            delete(indexWriter,id);
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
    public void update(ArticleDetailModel detailModel) {
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
    public IndexWriter getIndexWriter() {
        return indexService.getIndexWriter(direct);
    }

    @Override
    public IndexReader getIndexReader() {
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
    public Document getDocument(ArticleDetailModel detailModel) {
        // 创建一个存储对象
        Document doc = new Document();
        String html = detailModel.getHtml();
        Long id = detailModel.getId();
        if(id == null) return  null;
        doc.add(new StoredField("type","article"));
        doc.add(new LongPoint("id",id ));
        doc.add(new StoredField("id",id));
        doc.add(new TextField("title", detailModel.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", StringUtils.removeHtmlTags(html), Field.Store.YES));
        doc.add(new IntPoint("source_type",detailModel.getSourceType().getValue()));
        doc.add(new StringField("source_data",detailModel.getSourceData(),Field.Store.NO));
        List<TagDetailModel> tagList = detailModel.getTags();
        for (TagDetailModel model : tagList){
            doc.add(new LongPoint("tags",model.getId()));
        }
        doc.add(new IntPoint("comment_count",detailModel.getCommentCount()));
        doc.add(new IntPoint("view",detailModel.getView()));
        doc.add(new IntPoint("support",detailModel.getSupport()));
        doc.add(new IntPoint("oppose",detailModel.getOppose()));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new LongPoint("created_time",detailModel.getCreatedTime()));
        doc.add(new LongPoint("updated_time",detailModel.getUpdatedTime()));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));
        ColumnInfoModel columnModel = detailModel.getColumn();
        Long columnId = columnModel != null ? columnModel.getId() : 0l;
        doc.add(new LongPoint("column_id",columnId));
        doc.add(new StoredField("column_id",columnId));
        double score = 1.0;
        score += detailModel.getCommentCount() / 50.0;
        score += detailModel.getView() / 1000.0;
        score += detailModel.getSupport() / 500.0;
        score -= detailModel.getOppose() / 500.0;
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
        ArticleIndexModel model = new ArticleIndexModel();

        String id = document.get("id");
        model.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : 0);
        model.setTitle(document.get("title"));
        model.setContent(document.get("content"));
        String userId = document.get("created_user_id");
        model.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        String columnId = document.get("column_id");
        model.setColumnId(StringUtils.isNotEmpty(columnId) ? Long.parseLong(columnId) : 0);
        return model;
    }

    @Override
    @Async
    public void createIndex(Long id) {
        create(searchService.getDetail(id));
    }

    @Override
    @Async
    public void createIndex(ArticleDetailModel detailModel) {
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        update(searchService.getDetail(id));
    }

    @Override
    @Async
    public void updateIndex(ArticleDetailModel detailModel) {
        update(detailModel);
    }
}
