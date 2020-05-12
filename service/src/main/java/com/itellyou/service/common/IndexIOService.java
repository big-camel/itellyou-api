package com.itellyou.service.common;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

public interface IndexIOService {

    IndexWriter getIndexWriter();

    IndexReader getIndexReader();

    IndexWriter getIndexWriter(String direct);

    IndexReader getIndexReader(String direct);

    void closeIndexWriter();
}
