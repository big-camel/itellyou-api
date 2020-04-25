package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleIndexModel;
import com.itellyou.model.column.ColumnIndexModel;
import com.itellyou.model.question.QuestionAnswerIndexModel;
import com.itellyou.model.question.QuestionIndexModel;
import com.itellyou.model.tag.TagIndexModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserIndexModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleIndexService;
import com.itellyou.service.column.ColumnIndexService;
import com.itellyou.service.question.QuestionAnswerIndexService;
import com.itellyou.service.question.QuestionIndexService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.tag.TagIndexService;
import com.itellyou.service.user.UserIndexService;
import com.itellyou.util.StringUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class SearchController {

    private final QuestionIndexService questionIndexService;
    private final QuestionAnswerIndexService answerIndexService;
    private final ArticleIndexService articleIndexService;
    private final ColumnIndexService columnIndexService;
    private final TagIndexService tagIndexService;
    private final UserIndexService userIndexService;
    private final EntityService entityService;

    @Autowired
    public SearchController(QuestionIndexService questionIndexService, QuestionAnswerIndexService answerIndexService, ArticleIndexService articleIndexService, ColumnIndexService columnIndexService, TagIndexService tagIndexService, UserIndexService userIndexService, EntityService entityService){
        this.questionIndexService = questionIndexService;
        this.answerIndexService = answerIndexService;
        this.articleIndexService = articleIndexService;
        this.columnIndexService = columnIndexService;
        this.tagIndexService = tagIndexService;
        this.userIndexService = userIndexService;
        this.entityService = entityService;
    }

    private String getHighlighter(Highlighter highlighter,Analyzer analyzer,String fieldName,String fieldContent) throws IOException, InvalidTokenOffsetsException {
        String content = highlighter.getBestFragment(analyzer,fieldName,fieldContent);
        if(StringUtils.isNotEmpty(content)) return content + (content.length() < fieldContent.length() ? "..." : "");
        return StringUtils.getFragmenter(fieldContent);
    }

    @GetMapping("/search")
    public Result search(UserInfoModel userModel , @RequestParam String q, @RequestParam(required = false) String t, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        try {
            if(offset == null) offset = 0;
            if(limit == null) limit = 10;

            Long searchId = userModel == null ? null : userModel.getId();

            if(!StringUtils.isNotEmpty(q)) return new Result(0,"q is not Empty");
            String[] fields = {"name","title", "content"};
            IndexSearcher searcher;
            if(StringUtils.isNotEmpty(t)){
                switch (EntityType.valueOf(t.toUpperCase())){
                    case QUESTION:
                        searcher = new IndexSearcher(questionIndexService.getIndexReader());
                        break;
                    case ANSWER:
                        searcher = new IndexSearcher(answerIndexService.getIndexReader());
                        break;
                    case ARTICLE:
                        searcher = new IndexSearcher(articleIndexService.getIndexReader());
                        break;
                    case COLUMN:
                        searcher = new IndexSearcher(columnIndexService.getIndexReader());
                        break;
                    case TAG:
                        searcher = new IndexSearcher(tagIndexService.getIndexReader());
                        break;
                    case USER:
                        searcher = new IndexSearcher(userIndexService.getIndexReader());
                        break;
                    default:
                        return new Result(0,"Error Type");
                }
            }else{
                MultiReader multiReader = new MultiReader(questionIndexService.getIndexReader(),
                        answerIndexService.getIndexReader(),
                        articleIndexService.getIndexReader(),
                        columnIndexService.getIndexReader(),
                        tagIndexService.getIndexReader(),
                        userIndexService.getIndexReader());
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
            Map<String,Object> highlightData = new LinkedHashMap<>();
            Map<EntityType,HashSet<Long>> idsMap = new LinkedHashMap<>();
            for (int i = 0;i < limit;i ++) {
                Integer index = i + offset;
                if(index >= topDocs.scoreDocs.length) break;
                ScoreDoc doc = topDocs.scoreDocs[index];
                Document document = searcher.doc(doc.doc);
                String type = document.get("type");
                if(!StringUtils.isNotEmpty(type)) type = "";
                switch (EntityType.valueOf(type.toUpperCase())){
                    case QUESTION:
                        QuestionIndexModel questionIndexModel = questionIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.QUESTION)){
                            idsMap.get(EntityType.QUESTION).add(questionIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.QUESTION,new LinkedHashSet<Long>(){{ add(questionIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.QUESTION.toString()+"_" + questionIndexModel.getId(),new HashMap<String,String>(){{
                            put("title",getHighlighter(highlighter,analyzer,"title",questionIndexModel.getTitle()));
                            put("content",getHighlighter(highlighter,analyzer,"content",questionIndexModel.getContent()));
                        }});
                        break;
                    case ANSWER:
                        QuestionAnswerIndexModel answerIndexModel = answerIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.ANSWER)){
                            idsMap.get(EntityType.ANSWER).add(answerIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.ANSWER,new LinkedHashSet<Long>(){{ add(answerIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.ANSWER.toString()+"_" + answerIndexModel.getId(),new HashMap<String,String>(){{
                            put("title",getHighlighter(highlighter,analyzer,"title",answerIndexModel.getTitle()));
                            put("content",getHighlighter(highlighter,analyzer,"content",answerIndexModel.getContent()));
                        }});
                        break;
                    case ARTICLE:
                        ArticleIndexModel articleIndexModel = articleIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.ARTICLE)){
                            idsMap.get(EntityType.ARTICLE).add(articleIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.ARTICLE,new LinkedHashSet<Long>(){{ add(articleIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.ARTICLE.toString()+"_" + articleIndexModel.getId(),new HashMap<String,String>(){{
                            put("title",getHighlighter(highlighter,analyzer,"title",articleIndexModel.getTitle()));
                            put("content",getHighlighter(highlighter,analyzer,"content",articleIndexModel.getContent()));
                        }});
                        break;
                    case COLUMN:
                        ColumnIndexModel columnIndexModel = columnIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.COLUMN)){
                            idsMap.get(EntityType.COLUMN).add(columnIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.COLUMN,new LinkedHashSet<Long>(){{ add(columnIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.COLUMN.toString()+"_" + columnIndexModel.getId(),new HashMap<String,String>(){{
                            put("name",getHighlighter(highlighter,analyzer,"name",columnIndexModel.getName()));
                            put("description",getHighlighter(highlighter,analyzer,"description",columnIndexModel.getDescription()));
                        }});
                        break;
                    case TAG:
                        TagIndexModel tagIndexModel = tagIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.TAG)){
                            idsMap.get(EntityType.TAG).add(tagIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.TAG,new LinkedHashSet<Long>(){{ add(tagIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.TAG.toString()+"_" + tagIndexModel.getId(),new HashMap<String,String>(){{
                            put("name",getHighlighter(highlighter,analyzer,"name",tagIndexModel.getName()));
                            put("content",getHighlighter(highlighter,analyzer,"content",tagIndexModel.getContent()));
                        }});
                        break;
                    case USER:
                        UserIndexModel userIndexModel = userIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.USER)){
                            idsMap.get(EntityType.USER).add(userIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.USER,new LinkedHashSet<Long>(){{ add(userIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.USER.toString()+"_" + userIndexModel.getId(),new HashMap<String,String>(){{
                            put("name",getHighlighter(highlighter,analyzer,"name",userIndexModel.getName()));
                            put("description",getHighlighter(highlighter,analyzer,"description",userIndexModel.getDescription()));
                        }});
                        break;
                    default:
                        break;
                }
            }
            List<Map<String , Object >> listData = new ArrayList<>();
            Map<EntityType,Map<Long,Object>> mapData = entityService.find(idsMap,searchId,null);
            for (Map.Entry<String,Object> entry : highlightData.entrySet()){
                String[] keys = entry.getKey().split("_");
                EntityType type = EntityType.valueOf(keys[0].toUpperCase());
                Long id = Long.parseLong(keys[1]);
                if(mapData.containsKey(type) && mapData.get(type).containsKey(id)){
                    Map<String,Object> data = new HashMap<>();
                    data.put("type",type.toString());
                    data.put("object", mapData.get(type).get(id));
                    data.put("highlight",entry.getValue());
                    listData.add(data);
                }
            }
            Integer total = topDocs.scoreDocs.length;
            total = total > maxLimit ? maxLimit : total;
            PageModel pageModel = new PageModel(offset,limit,total,listData);
            Map<String,Object> extendMap = new HashMap<>();
            extendMap.put("hits",topDocs.totalHits.value);
            pageModel.setExtend(extendMap);
            return new Result(pageModel);
        }catch (Exception e){
            return new Result(0,e.getMessage());
        }
    }
}
