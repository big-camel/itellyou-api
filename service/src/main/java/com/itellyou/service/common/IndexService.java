package com.itellyou.service.common;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

public interface IndexService {

    IndexWriter getIndexWriter(String direct);

    IndexReader getIndexReader(String direct);
}
