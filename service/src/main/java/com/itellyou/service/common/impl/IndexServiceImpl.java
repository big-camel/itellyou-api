package com.itellyou.service.common.impl;

import com.itellyou.model.common.IndexModel;
import com.itellyou.service.common.IndexIOService;
import com.itellyou.service.common.IndexService;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class IndexServiceImpl<T> implements IndexService<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IndexIOService indexIOService;

    public IndexServiceImpl(String direct){
        this.indexIOService = new IndexIOServiceImpl(direct);
    }

    @Override
    @Async
    public void create(T detailModel) {
        try {
            if(detailModel == null) throw new Exception("创建索引对象不能为空");
            IndexWriter indexWriter = getIndexWriter();
            indexWriter.addDocument(getDocument(detailModel));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }finally {
            indexIOService.closeIndexWriter();
        }
    }
    @Override
    public void create(IndexWriter indexWriter, T detailModel) {
        try {
            indexWriter.addDocument(getDocument(detailModel));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
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
            logger.error(e.getLocalizedMessage());
        }finally {
            indexIOService.closeIndexWriter();
        }
    }

    @Override
    @Async
    public void delete(Collection<Long> ids) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            for (Long id : ids){
                delete(indexWriter,id);
            }
            indexWriter.close();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }finally {
            indexIOService.closeIndexWriter();
        }
    }

    @Override
    public void delete(IndexWriter indexWriter , Long id) {
        try {
            indexWriter.deleteDocuments(LongPoint.newExactQuery("id",id));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public Long getId(T detailModel) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = detailModel.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField("id");
                if (field != null) {
                    field.setAccessible(true);
                    return Long.valueOf(field.get(detailModel).toString());
                }
            }catch (NoSuchFieldException e){
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    @Override
    @Async
    public void update(T detailModel) {
        try {
            if(detailModel == null) throw new Exception("更新索引对象不能为空");
            IndexWriter indexWriter = getIndexWriter();
            Long id = getId(detailModel);
            if(id != null) delete(indexWriter,id);
            create(indexWriter,detailModel);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }finally {
            indexIOService.closeIndexWriter();
        }
    }

    @Override
    @Async
    public void update(List<T> models) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            for (T model : models){
                Long id = getId(model);
                if(id != null) delete(indexWriter,id);
                create(indexWriter,model);
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }finally {
            indexIOService.closeIndexWriter();
        }
    }

    @Override
    public IndexWriter getIndexWriter() {
        return indexIOService.getIndexWriter();
    }

    @Override
    public IndexReader getIndexReader() {
        return indexIOService.getIndexReader();
    }

    @Override
    public Document getDocument(Long id) {
        try {
            IndexReader indexReader = getIndexReader();
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs docs = searcher.search(LongPoint.newExactQuery("id", id), 1);
            if (docs != null && docs.scoreDocs.length > 0) return searcher.doc(docs.scoreDocs[0].doc);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Document getDocument(T detailModel) {
        try {
            // 创建一个存储对象
            Document doc = new Document();
            Long id = getId(detailModel);
            if (id == null) throw new Exception("id is null");
            doc.add(new LongPoint("id",id ));
            doc.add(new StoredField("id",id));
            return doc;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public IndexModel getModel(Document document) {
        IndexModel model = new IndexModel();
        String id = document.get("id");
        model.setId(StringUtils.isNotEmpty(id) ? Long.parseLong(id) : null);
        return model;
    }

    @Override
    @Async
    public void createIndex(Long id) {

    }

    @Override
    @Async
    public void createIndex(T detailModel) {
        create(detailModel);
    }

    @Override
    @Async
    public void updateIndex(Long id) {

    }

    @Async
    @Override
    public void updateIndex(Collection<Long> ids) {

    }

    @Override
    @Async
    public void updateIndex(T detailModel) {
        update(detailModel);
    }
}
