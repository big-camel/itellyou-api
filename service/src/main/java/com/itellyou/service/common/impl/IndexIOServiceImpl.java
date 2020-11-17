package com.itellyou.service.common.impl;

import com.itellyou.service.common.IndexIOService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IndexIOServiceImpl implements IndexIOService {

    private final Logger logger = LoggerFactory.getLogger(IndexIOServiceImpl.class);
    private Lock lock = new ReentrantLock();
    private final String direct;
    private IndexWriter indexWriter = null;
    private IndexReader indexReader = null;
    private Long prevReader = 0l;

    public IndexIOServiceImpl(String direct) {
        this.direct = direct;
    }

    @Override
    public IndexWriter getIndexWriter() {
        return getIndexWriter(direct);
    }

    @Override
    public IndexReader getIndexReader() {
        return getIndexReader(direct);
    }

    @Override
    public IndexWriter getIndexWriter(String direct) {
        try {
            lock.lock();
            Directory directory = FSDirectory.open(Paths.get(direct));
            Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj);
            IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
            iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(directory, iwConfig);
            return indexWriter;
        } catch (LockObtainFailedException exception) {
            logger.error(exception.getLocalizedMessage());
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public IndexReader getIndexReader(String direct) {
        try {
            if (indexReader != null) {
                Long now = DateUtils.getTimestamp();
                // 距离上次获取Reader大于一小时则尝试获取最新的Reader
                if(now - prevReader > 3600){
                    IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) indexReader);
                    prevReader = now;
                    if(newReader != null) {
                        indexReader.close();
                        indexReader = newReader;
                    }
                }
                return indexReader;
            }
            Directory directory = FSDirectory.open(Paths.get(direct));
            indexReader = DirectoryReader.open(directory);
            return indexReader;
        }
        catch (IndexNotFoundException e){
            try{
                logger.warn(e.getLocalizedMessage());
                getIndexWriter(direct);
                this.closeIndexWriter();
                return getIndexReader(direct);
            }catch (Exception ex){
                logger.error(ex.getLocalizedMessage());
                return null;
            }
        }
        catch (Exception e){
            logger.error(e.getLocalizedMessage());
            indexReader = null;
            return getIndexReader();
        }
    }

    @Override
    public void closeIndexWriter() {
        try {
            if (indexWriter != null && indexWriter.isOpen()) {
                indexWriter.close();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }finally {
            lock.unlock();
        }
    }
}
