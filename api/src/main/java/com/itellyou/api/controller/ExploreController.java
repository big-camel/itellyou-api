package com.itellyou.api.controller;

import com.itellyou.model.common.IndexModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntitySearchModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.common.impl.IndexFactory;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;

@Validated
@RestController
@RequestMapping("/explore")
public class ExploreController {

    private final EntityService entityService;
    private final UserSearchService userSearchService;
    private final IndexFactory indexFactory;

    public ExploreController(EntityService entityService, UserSearchService userSearchService, IndexFactory indexFactory) {
        this.entityService = entityService;
        this.userSearchService = userSearchService;
        this.indexFactory = indexFactory;
    }

    @GetMapping("/recommends")
    public ResultModel recommends(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(offset == null) offset = 0;
        if(limit == null) limit = 20;
        Long searchId = userModel == null ? null : userModel.getId();
        try {
            MultiReader multiReader = new MultiReader(indexFactory.create(EntityType.QUESTION).getIndexReader(),
                    indexFactory.create(EntityType.ARTICLE).getIndexReader());
            IndexSearcher searcher = new IndexSearcher(multiReader);

            Long now = DateUtils.getTimestamp();
            Long beginTime = now - 360 * 86400;

            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            Query dateQuery = LongPoint.newRangeQuery("updated_time",beginTime,now);
            booleanQuery.add(dateQuery, BooleanClause.Occur.MUST);
            booleanQuery.add(IntPoint.newExactQuery("answer_count",0),BooleanClause.Occur.MUST_NOT);

            Query query = FunctionScoreQuery.boostByValue(booleanQuery.build(), DoubleValuesSource.fromDoubleField("score"));

            Integer maxLimit = 10000;

            SortField sortIdDoc = new SortField("id",SortField.Type.DOC,true);
            Sort sort = new Sort(sortIdDoc);
            TopDocs topDocs = searcher.search(query, maxLimit,sort);

            List<IndexModel> indexModels = new LinkedList<>();
            int countF = 0;
            for (int i = 0;i < limit;i ++) {
                Integer index = i + offset;
                if (index >= topDocs.scoreDocs.length) break;
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
                if(type.equals(EntityType.QUESTION)) args.put("childCount",1);
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
                }});
            }
            Integer total = topDocs.scoreDocs.length;
            total = total - countF;
            total = total > maxLimit ? maxLimit : total;
            limit = limit - countF;
            PageModel pageModel = new PageModel(offset,limit,total,listData);
            return new ResultModel(pageModel);
        }catch (Exception e){
            return new ResultModel(500,e.getLocalizedMessage());
        }
    }

    @GetMapping("/writer")
    public ResultModel writer(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 20;
        Long searchId = userModel == null ? null : userModel.getId();
        Map<String,String> order = new HashMap<>();
        order.put("article_count","desc");
        order.put("column_count","desc");
        order.put("follower_count","desc");
        PageModel<UserDetailModel> pageModel = userSearchService.page(null,searchId,null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(pageModel);
    }

    @GetMapping("/answerer")
    public ResultModel answerer(UserInfoModel userModel, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 20;
        Long searchId = userModel == null ? null : userModel.getId();
        Map<String,String> order = new HashMap<>();
        order.put("answer_count","desc");
        order.put("follower_count","desc");
        PageModel<UserDetailModel> pageModel = userSearchService.page(null,searchId,null,null,null,null,null,null,null,order,offset,limit);
        return new ResultModel(pageModel);
    }
}
