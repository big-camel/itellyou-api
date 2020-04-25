package com.itellyou.api.controller;

import com.itellyou.api.handler.response.Result;
import com.itellyou.model.article.ArticleIndexModel;
import com.itellyou.model.question.QuestionIndexModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticleIndexService;
import com.itellyou.service.question.QuestionAnswerIndexService;
import com.itellyou.service.question.QuestionIndexService;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Validated
@RestController
@RequestMapping("/explore")
public class ExploreController {

    private final QuestionIndexService questionIndexService;
    private final QuestionAnswerIndexService answerIndexService;
    private final ArticleIndexService articleIndexService;
    private final EntityService entityService;
    private final UserSearchService userSearchService;

    public ExploreController(QuestionIndexService questionIndexService, QuestionAnswerIndexService answerIndexService, ArticleIndexService articleIndexService, EntityService entityService, UserSearchService userSearchService) {
        this.questionIndexService = questionIndexService;
        this.answerIndexService = answerIndexService;
        this.articleIndexService = articleIndexService;
        this.entityService = entityService;
        this.userSearchService = userSearchService;
    }

    @GetMapping("/recommends")
    public Result recommends(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(offset == null) offset = 0;
        if(limit == null) limit = 20;
        Long searchId = userModel == null ? null : userModel.getId();
        try {
            MultiReader multiReader = new MultiReader(questionIndexService.getIndexReader(),
                    //answerIndexService.getIndexReader(),
                    articleIndexService.getIndexReader());
            IndexSearcher searcher = new IndexSearcher(multiReader);

            Long now = DateUtils.getTimestamp();
            Long beginTime = now - 30 * 86400;

            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            Query dateQuery = LongPoint.newRangeQuery("updated_time",beginTime,now);
            booleanQuery.add(dateQuery, BooleanClause.Occur.MUST);
            booleanQuery.add(IntPoint.newExactQuery("answers",0),BooleanClause.Occur.MUST_NOT);

            Query query = FunctionScoreQuery.boostByValue(booleanQuery.build(), DoubleValuesSource.fromDoubleField("score"));

            Integer maxLimit = 10000;

            SortField sortIdDoc = new SortField("id",SortField.Type.DOC,true);
            Sort sort = new Sort(sortIdDoc);
            TopDocs topDocs = searcher.search(query, maxLimit,sort);

            Map<String,Object> dataMap = new LinkedHashMap<>();
            Map<EntityType,HashSet<Long>> idsMap = new LinkedHashMap<>();
            int countF = 0;
            for (int i = 0;i < limit;i ++) {
                Integer index = i + offset;
                if (index >= topDocs.scoreDocs.length) break;
                ScoreDoc doc = topDocs.scoreDocs[index];
                Document document = searcher.doc(doc.doc);
                String type = document.get("type");
                if(!StringUtils.isNotEmpty(type)) type = "";
                switch (EntityType.valueOf(type.toUpperCase())) {
                    case QUESTION:
                        QuestionIndexModel questionIndexModel = questionIndexService.getModel(document);
                        if (idsMap.containsKey(EntityType.QUESTION)) {
                            idsMap.get(EntityType.QUESTION).add(questionIndexModel.getId());
                        } else {
                            idsMap.put(EntityType.QUESTION, new LinkedHashSet<Long>() {{
                                add(questionIndexModel.getId());
                            }});
                        }
                        dataMap.put(EntityType.QUESTION.toString()+"_" + questionIndexModel.getId(),questionIndexModel);
                        break;
                    /**case ANSWER:
                        QuestionAnswerIndexModel answerIndexModel = answerIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.ANSWER)){
                            idsMap.get(EntityType.ANSWER).add(answerIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.ANSWER,new LinkedHashSet<Long>(){{ add(answerIndexModel.getId());}});
                        }
                        if(idsMap.containsKey(EntityType.QUESTION) && idsMap.get(EntityType.QUESTION).contains(answerIndexModel.getQuestionId())){
                            limit++;
                            countF++;
                        }
                        dataMap.put(EntityType.ANSWER.toString()+"_" + answerIndexModel.getId(),answerIndexModel);
                        break;**/
                    case ARTICLE:
                        ArticleIndexModel articleIndexModel = articleIndexService.getModel(document);
                        if(idsMap.containsKey(EntityType.ARTICLE)){
                            idsMap.get(EntityType.ARTICLE).add(articleIndexModel.getId());
                        }else{
                            idsMap.put(EntityType.ARTICLE,new LinkedHashSet<Long>(){{ add(articleIndexModel.getId());}});
                        }
                        dataMap.put(EntityType.ARTICLE.toString()+"_" + articleIndexModel.getId(),articleIndexModel);
                        break;
                    default:
                        System.out.println(document);
                }
            }
            List<Map<String , Object >> listData = new ArrayList<>();
            Map<EntityType,Map<Long,Object>> mapData = entityService.find(idsMap,searchId,1);
            for (Map.Entry<String,Object> entry : dataMap.entrySet()){
                String[] keys = entry.getKey().split("_");
                EntityType type = EntityType.valueOf(keys[0].toUpperCase());
                Long id = Long.parseLong(keys[1]);
                if(mapData.containsKey(type) && mapData.get(type).containsKey(id)){
                    Map<String,Object> data = new HashMap<>();
                    data.put("type",type.toString());
                    data.put("object",mapData.get(type).get(id));
                    listData.add(data);
                }
            }
            Integer total = topDocs.scoreDocs.length;
            total = total - countF;
            total = total > maxLimit ? maxLimit : total;
            limit = limit - countF;
            PageModel pageModel = new PageModel(offset,limit,total,listData);
            multiReader.close();
            return new Result(pageModel);
        }catch (Exception e){
            return new Result(500,e.getMessage());
        }
    }

    @GetMapping("/writer")
    public Result writer(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 20;
        Long searchId = userModel == null ? null : userModel.getId();
        Map<String,String> order = new HashMap<>();
        order.put("article_count","desc");
        order.put("column_count","desc");
        order.put("follower_count","desc");
        PageModel<UserDetailModel> pageModel = userSearchService.page(null,searchId,null,null,null,null,null,null,null,order,offset,limit);
        return new Result(pageModel);
    }

    @GetMapping("/answerer")
    public Result answerer(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 20;
        Long searchId = userModel == null ? null : userModel.getId();
        Map<String,String> order = new HashMap<>();
        order.put("answer_count","desc");
        order.put("follower_count","desc");
        PageModel<UserDetailModel> pageModel = userSearchService.page(null,searchId,null,null,null,null,null,null,null,order,offset,limit);
        return new Result(pageModel);
    }
}
