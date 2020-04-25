package com.itellyou.service.common.impl;

import com.itellyou.service.common.IndexService;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class IndexServiceImpl implements IndexService {

    private IndexWriter indexWriter = null;

    @Override
    public IndexWriter getIndexWriter(String direct) {
        try {
            if (indexWriter != null && indexWriter.isOpen()) return indexWriter;
            while (true) {
                try {
                    if (indexWriter != null) indexWriter.close();

                    Directory directory = FSDirectory.open(Paths.get(direct));
                    Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj);
                    IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
                    iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                    indexWriter = new IndexWriter(directory, iwConfig);
                    return indexWriter;
                } catch (LockObtainFailedException exception) {
                    Thread.sleep(1000l);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public IndexReader getIndexReader(String direct) {
        try {
            Directory directory = FSDirectory.open(Paths.get(direct));
            return DirectoryReader.open(directory);
        }
        catch (IndexNotFoundException e){
            try{
                IndexWriter indexWriter = getIndexWriter(direct);
                indexWriter.close();
                return getIndexReader(direct);
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
