package com.itellyou.service.column.impl;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnIndexModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagInfoModel;
import com.itellyou.service.column.ColumnIndexService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.common.IndexService;
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
import java.util.List;

@Service
public class ColumnIndexServiceImpl implements ColumnIndexService {
    private final String direct = "./.indexer/column";

    private final IndexService indexService;
    private final ColumnSearchService searchService;

    @Autowired
    public ColumnIndexServiceImpl(IndexService indexService, ColumnSearchService searchService){
        this.indexService = indexService;
        this.searchService = searchService;
    }

    @Override
    @Async
    public void create(ColumnDetailModel detailModel) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            create(indexWriter,detailModel);
            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void create(IndexWriter indexWriter, ColumnDetailModel detailModel) {
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
    public void delete(IndexWriter indexWriter , Long id) {
        try {
            indexWriter.deleteDocuments(LongPoint.newExactQuery("id",id));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void update(ColumnDetailModel detailModel) {
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
    public IndexWriter getIndexWriter(){
        return indexService.getIndexWriter(direct);
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
    public Document getDocument(ColumnDetailModel detailModel) {
        Document doc = new Document();
        Long id = detailModel.getId();
        if(id == null) return null;
        doc.add(new StoredField("type","column"));
        doc.add(new LongPoint("id", id));
        doc.add(new StoredField("id",id));
        doc.add(new TextField("name", detailModel.getName(), Field.Store.YES));
        doc.add(new TextField("description", StringUtils.removeHtmlTags(detailModel.getDescription()), Field.Store.YES));
        List<TagInfoModel> tagList = detailModel.getTags();
        for (TagInfoModel model : tagList){
            doc.add(new LongPoint("tags",model.getId()));
        }
        doc.add(new IntPoint("article_count",detailModel.getArticleCount()));
        doc.add(new IntPoint("star_count",detailModel.getStarCount()));
        doc.add(new LongPoint("created_time",detailModel.getCreatedTime()));
        doc.add(new LongPoint("updated_time",detailModel.getUpdatedTime()));
        doc.add(new LongPoint("created_user_id",detailModel.getAuthor().getId()));

        double score = 1.0;
        score += detailModel.getArticleCount() / 100.0;
        score += detailModel.getStarCount() / 100.0;
        doc.add(new DoubleDocValuesField("score",score));
        return doc;
    }

    @Override
    public ColumnIndexModel getModel(Document document) {
        ColumnIndexModel model = new ColumnIndexModel();
        String id = document.get("id");
        model.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : 0);
        model.setName(document.get("name"));
        model.setDescription(document.get("description"));
        String userId = document.get("created_user_id");
        model.setCreatedUserId(StringUtils.isNotEmpty(userId) ? Long.parseLong(userId) : 0);
        return model;
    }

    @Override
    @Async
    public void createIndex(Long id) {
        create(searchService.getDetail(id));
    }

    @Override
    public void createIndex(ColumnDetailModel model) {
        create(model);
    }

    @Override
    @Async
    public void updateIndex(Long id) {
        update(searchService.getDetail(id));
    }

    @Override
    @Async
    public void updateIndex(ColumnDetailModel model) {
        update(model);
    }
}
