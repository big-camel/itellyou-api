package com.itellyou.api.controller;

import com.itellyou.model.article.ArticlePaidReadModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.question.*;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.article.ArticleIndexModel;
import com.itellyou.model.column.ColumnIndexModel;
import com.itellyou.model.tag.TagDetailModel;
import com.itellyou.model.tag.TagIndexModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserIndexModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.impl.IndexFactory;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.sys.EntityService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class SearchController {

    private final IndexService<QuestionDetailModel> questionIndexService;
    private final IndexService<QuestionAnswerDetailModel> answerIndexService;
    private final IndexService<ArticleDetailModel> articleIndexService;
    private final IndexService<ColumnDetailModel> columnIndexService;
    private final IndexService<TagDetailModel> tagIndexService;
    private final IndexService<UserInfoModel> userIndexService;
    private final EntityService entityService;

    @Autowired
    public SearchController( EntityService entityService){
        this.questionIndexService = IndexFactory.create(EntityType.QUESTION);
        this.answerIndexService = IndexFactory.create(EntityType.ANSWER);
        this.articleIndexService = IndexFactory.create(EntityType.ARTICLE);
        this.columnIndexService = IndexFactory.create(EntityType.COLUMN);
        this.tagIndexService = IndexFactory.create(EntityType.TAG);
        this.userIndexService = IndexFactory.create(EntityType.USER);
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
                        return new ResultModel(0,"Error Type");
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
                        QuestionIndexModel questionIndexModel = (QuestionIndexModel)questionIndexService.getModel(document);
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
                        QuestionAnswerIndexModel answerIndexModel = (QuestionAnswerIndexModel) answerIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.ANSWER)){
                            idsMap.get(EntityType.ANSWER).add(answerIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.ANSWER,new LinkedHashSet<Long>(){{ add(answerIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.ANSWER.toString()+"_" + answerIndexModel.getId(),new HashMap<String,String>(){{
                            put("title",getHighlighter(highlighter,analyzer,"title",answerIndexModel.getTitle()));
                            put("content",answerIndexModel.getContent());
                        }});
                        break;
                    case ARTICLE:
                        ArticleIndexModel articleIndexModel = (ArticleIndexModel) articleIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.ARTICLE)){
                            idsMap.get(EntityType.ARTICLE).add(articleIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.ARTICLE,new LinkedHashSet<Long>(){{ add(articleIndexModel.getId());}});
                        }
                        highlightData.put(EntityType.ARTICLE.toString()+"_" + articleIndexModel.getId(),new HashMap<String,String>(){{
                            put("title",getHighlighter(highlighter,analyzer,"title",articleIndexModel.getTitle()));
                            put("content",articleIndexModel.getContent());
                        }});
                        break;
                    case COLUMN:
                        ColumnIndexModel columnIndexModel = (ColumnIndexModel) columnIndexService.getModel(document);
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
                        TagIndexModel tagIndexModel = (TagIndexModel) tagIndexService.getModel(document);
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
                        UserIndexModel userIndexModel = (UserIndexModel) userIndexService.getModel(document);
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
                    Object object = mapData.get(type).get(id);
                    data.put("object", object);
                    Object highlight = entry.getValue();
                    if(type.equals(EntityType.ARTICLE)){
                        ArticleDetailModel articleDetailModel = (ArticleDetailModel)object;
                        ArticlePaidReadModel articlePaidReadModel = articleDetailModel.getPaidRead();

                        HashMap<String,String> highlightMap = (HashMap<String,String>)highlight;
                        String content = highlightMap.get("content");

                        if(articlePaidReadModel != null){
                            int len = new BigDecimal(content.length()).multiply(new BigDecimal(articlePaidReadModel.getFreeReadScale())).intValue();
                            if(len >= content.length()) len = content.length() - 1;
                            if(len <= 0) content = "";
                            else {
                                content = content.substring(0, len);
                            }
                        }
                        highlightMap.put("content",getHighlighter(highlighter,analyzer,"content",StringUtils.getFragmenter(content)));
                    }else if(type.equals(EntityType.ANSWER)){
                        QuestionAnswerDetailModel answerDetailModel = (QuestionAnswerDetailModel)object;
                        QuestionAnswerPaidReadModel answerPaidReadModel = answerDetailModel.getPaidRead();

                        HashMap<String,String> highlightMap = (HashMap<String,String>)highlight;
                        String content = highlightMap.get("content");

                        if(answerPaidReadModel != null){
                            int len = new BigDecimal(content.length()).multiply(new BigDecimal(answerPaidReadModel.getFreeReadScale())).intValue();
                            if(len >= content.length()) len = content.length() - 1;
                            if(len <= 0) content = "";
                            else {
                                content = content.substring(0, len);
                            }
                        }
                        highlightMap.put("content",getHighlighter(highlighter,analyzer,"content",StringUtils.getFragmenter(content)));
                    }
                    data.put("highlight",highlight);
                    listData.add(data);
                }
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
