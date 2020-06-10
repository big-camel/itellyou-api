package com.itellyou.api.controller.question;

import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.question.QuestionDetailModel;
import com.itellyou.model.question.QuestionIndexModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.impl.IndexFactory;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.common.StarService;
import com.itellyou.service.question.QuestionSearchService;
import com.itellyou.service.tag.impl.TagStarServiceImpl;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

@Validated
@RestController
@RequestMapping("/question")
public class QuestionSearchController {

    private final IndexService<QuestionDetailModel> questionIndexService;
    private final QuestionSearchService questionSearchService;
    private final StarService<TagStarModel> tagStarService;

    @Autowired
    public QuestionSearchController(QuestionSearchService questionSearchService, TagStarServiceImpl tagStarService){
        this.questionIndexService = IndexFactory.create(EntityType.QUESTION);
        this.questionSearchService = questionSearchService;
        this.tagStarService = tagStarService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false,name = "user_id") Long userId, @RequestParam(required = false) String type, @RequestParam(required = false,name = "tag_id") Long tagId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer child) {
        Long searchUserId = userModel == null ? null : userModel.getId();
        PageModel<QuestionDetailModel> data = null;
        if(type == null) type = "";
        if(child != null && child > 5) child = 5;
        Map<String,String > order;
        switch (type){
            case "reward":
                order = new HashMap<>();
                order.put("reward_value","desc");
                data = questionSearchService.page(searchUserId,userId,true,false,false,null,true,child, null,1.0,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
                break;
            case "hot":
                order = new HashMap<>();

                order.put("support","desc");
                order.put("answers","desc");
                order.put("comments","desc");
                order.put("view","desc");
                order.put("star_count","desc");
                data = questionSearchService.page(searchUserId,userId,true,false,false,null,true,child, null,null,null,null,null,null,1,null,10,null,null,null,null,null,null,null,null,null,order,offset,limit);
                break;
            case "star":
                if(userModel == null) return new ResultModel(401,"未登陆");
                List<TagStarDetailModel> listTag = (List<TagStarDetailModel>) tagStarService.search(null,userModel.getId(),null,null,null,null,null,null);
                if(listTag.size() < 1) return new ResultModel(404,"尚未关注标签");
                HashSet<Long> tags = new LinkedHashSet<>();
                for (TagStarDetailModel tagModel: listTag) {
                    tags.add(tagModel.getTagId());
                }
                order = new HashMap<>();
                data = questionSearchService.page(searchUserId,userId,true,false,false,null,true,child,null,null,null,tags,null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
                break;
            default:
                order = new HashMap<>();
                order.put("created_time","desc");
                data = questionSearchService.page(searchUserId,userId,true,false,false,null,true,child,null,null,null,
                        tagId != null ? new HashSet<Long>(){{ add(tagId);}} : null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
        }
        return new ResultModel(data);
    }

    @GetMapping("/{id:\\d+}")
    public ResultModel detail(UserInfoModel userModel, @PathVariable Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        QuestionDetailModel detailModel = questionSearchService.getDetail(id,(Long)null,searchUserId);
        if(detailModel == null|| detailModel.isDeleted() || detailModel.isDisabled()) return  new ResultModel(404,"错误的编号");
        return new ResultModel(detailModel);
    }

    @GetMapping("/related")
    public ResultModel related(UserInfoModel userModel, @RequestParam @NotNull Long id, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        Long searchId = userModel == null ? null : userModel.getId();
        QuestionDetailModel detailModel = questionSearchService.getDetail(id);
        if(detailModel == null) return new ResultModel(404,"未知的提问");
        try {
            String[] fields = {"title", "content"};
            IndexSearcher searcher = new IndexSearcher(questionIndexService.getIndexReader());
            Analyzer analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.query_ansj);
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);

            String text = detailModel.getTitle() + detailModel.getDescription();
            Query query = queryParser.parse(QueryParser.escape(text));
            query = FunctionScoreQuery.boostByValue(query, DoubleValuesSource.fromDoubleField("score"));

            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(query, BooleanClause.Occur.MUST);
            booleanQuery.add(LongPoint.newExactQuery("id", detailModel.getId()), BooleanClause.Occur.MUST_NOT);

            Integer maxLimit = 100;
            TopDocs topDocs = searcher.search(booleanQuery.build(), maxLimit);

            HashSet<Long> ids = new LinkedHashSet<>();
            for (int i = 0;i < limit;i ++) {
                Integer index = i + offset;
                if (index >= topDocs.scoreDocs.length) break;
                ScoreDoc doc = topDocs.scoreDocs[index];
                Document document = searcher.doc(doc.doc);
                QuestionIndexModel docData = (QuestionIndexModel)questionIndexService.getModel(document);
                ids.add(docData.getId());
            }
            Integer total = topDocs.scoreDocs.length;
            total = total > maxLimit ? maxLimit : total;
            List<QuestionDetailModel> listData = new ArrayList<>();
            if(!ids.isEmpty()) {
                listData = questionSearchService.search(ids, null, null, searchId,false,null,  null, null, null, null, null);
            }
            PageModel pageModel = new PageModel(offset == 0,offset + limit >= total,offset,limit,total,listData);
            return new ResultModel(pageModel);
        }catch (Exception e){
            return new ResultModel(500,e.getMessage());
        }
    }
}
