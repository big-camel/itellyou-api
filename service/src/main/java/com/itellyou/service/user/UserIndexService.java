package com.itellyou.service.user;

import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.user.UserIndexModel;
import com.itellyou.model.user.UserInfoModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.Map;

public interface UserIndexService {

    @Async
    void create(UserInfoModel infoModel);

    void create(IndexWriter indexWriter , UserInfoModel detailModel);

    @Async
    void delete(Long id);

    void delete(IndexWriter indexWriter , Long id);

    @Async
    void update(UserInfoModel infoModel);

    IndexWriter getIndexWriter() throws InterruptedException;

    Document getDocument(UserInfoModel infoModel);

    UserIndexModel getModel(Document document);

    IndexReader getIndexReader() throws IOException;
}
