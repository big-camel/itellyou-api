package com.itellyou.api.controller;

import com.itellyou.model.common.IndexModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.common.impl.IndexFactory;
import com.itellyou.service.sys.EntityService;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.StringUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@RestController
public class SearchController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IndexFactory indexFactory;
    private final EntityService entityService;

    @Autowired
    public SearchController(IndexFactory indexFactory, EntityService entityService){
        this.indexFactory = indexFactory;
        this.entityService = entityService;
    }

    private String getHighlighter(Highlighter highlighter,Analyzer analyzer,String fieldName,String fieldContent) throws IOException, InvalidTokenOffsetsException {
        String content = highlighter.getBestFragment(analyzer,fieldName,fieldContent);
        if(StringUtils.isNotEmpty(content)) return content + (content.length() < fieldContent.length() ? "..." : "");
        return StringUtils.getFragmenter(fieldContent);
    }

    @GetMapping("/search")
    public ResultModel search(UserInfoModel userModel , @RequestParam String q, @RequestParam(required = false) String t, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        try {
            if(offset == null) offset = 0;
            if(limit == null) limit = 10;

            Long searchId = userModel == null ? null : userModel.getId();

            if(!StringUtils.isNotEmpty(q)) return new ResultModel(0,"q is not Empty");
            String[] fields = {"name","title","description", "content"};
            IndexSearcher searcher;
            if(StringUtils.isNotEmpty(t)){
                try{
                    searcher = new IndexSearcher(indexFactory.create(EntityType.valueOf(t.toUpperCase())).getIndexReader());
                }catch (Exception e){
                    logger.error(e.getLocalizedMessage());
                    return new ResultModel(500,e.getLocalizedMessage());
                }
            }else{
                MultiReader multiReader = new MultiReader(indexFactory.create(EntityType.QUESTION).getIndexReader(),
                        indexFactory.create(EntityType.ANSWER).getIndexReader(),
                        indexFactory.create(EntityType.ARTICLE).getIndexReader(),
                        indexFactory.create(EntityType.COLUMN).getIndexReader(),
                        indexFactory.create(EntityType.TAG).getIndexReader(),
                        indexFactory.create(EntityType.USER).getIndexReader());
                searcher = new IndexSearcher(multiReader);
            }

            Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.query_ansj);
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);

            Query query = queryParser.parse(QueryParser.escape(q));
            query = FunctionScoreQuery.boostByValue(query, DoubleValuesSource.fromDoubleField("score"));

            //创建一个高亮器
            Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<em>","</em>"), new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter());

            Integer maxLimit = 10000;

            TopDocs topDocs = searcher.search(query, maxLimit);

            List<IndexModel> indexModels = new LinkedList<>();
            for (int i = 0;i < limit;i ++) {
                Integer index = i + offset;
                if(index >= topDocs.scoreDocs.length) break;
                ScoreDoc doc = topDocs.scoreDocs[index];
                Document document = searcher.doc(doc.doc);
                String type = document.get("type");
                if(!StringUtils.isNotEmpty(type)) type = "";
                IndexService indexService = indexFactory.create(EntityType.valueOf(type.toUpperCase()));
                if(indexService != null){
                    IndexModel indexModel = indexService.getModel(document);
                    if(indexModel != null) indexModels.add(indexModel);
                }
            }

            EntityDataModel<CacheEntity> entityDataModel = entityService.search(indexModels,(IndexModel model, Function<EntityType,Map<String,Object>> getArgs) -> {
                EntityType type = model.getType();
                Map<String,Object> args = getArgs.apply(type);
                args.put("hasContent",false);
                args.put("searchUserId",searchId);
                HashSet ids = (HashSet)args.computeIfAbsent("ids",key -> new HashSet<>());
                if(!ids.contains(model.getId())) ids.add(model.getId());
                args.put("ids",ids);
                return new EntitySearchModel(type,args);
            });

            List<Map<String , Object >> listData = new ArrayList<>();
            for (IndexModel indexModel : indexModels){
                Object target = entityDataModel.get(indexModel.getType(),indexModel.getId());
                if(target == null) continue;
                listData.add(new HashMap<String,Object>(){{
                    put("type",indexModel.getType().toString());
                    put("object",target);
                    put("highlight",new HashMap<String,String>(){{
                        put(indexModel.getTitleField(),getHighlighter(highlighter,analyzer,indexModel.getTitleField(),indexModel.getTitle()));
                        put(indexModel.getContentField(),getHighlighter(highlighter,analyzer,indexModel.getContentField(),indexModel.getContent()));
                    }});
                }});
            }
            Integer total = topDocs.scoreDocs.length;
            total = total > maxLimit ? maxLimit : total;
            PageModel pageModel = new PageModel(offset,limit,total,listData);
            Map<String,Object> extendMap = new HashMap<>();
            extendMap.put("hits",topDocs.totalHits.value);
            pageModel.setExtend(extendMap);
            return new ResultModel(pageModel);
        }catch (Exception e){
            return new ResultModel(0,e.getMessage());
        }
    }
}
