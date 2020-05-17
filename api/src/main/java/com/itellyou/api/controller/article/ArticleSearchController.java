package com.itellyou.api.controller.article;

import com.itellyou.model.article.ArticleDetailModel;
import com.itellyou.model.article.ArticleIndexModel;
import com.itellyou.model.common.ResultModel;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.tag.TagStarDetailModel;
import com.itellyou.model.tag.TagStarModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.article.ArticlePaidReadService;
import com.itellyou.service.article.ArticleSearchService;
import com.itellyou.service.article.impl.ArticleIndexServiceImpl;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.common.StarService;
import com.itellyou.service.tag.impl.TagStarServiceImpl;
import com.itellyou.util.HtmlUtils;
import com.itellyou.util.ansj.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

@Validated
@RestController
@RequestMapping("/article")
public class ArticleSearchController {

    private final ArticleSearchService searchService;
    private final IndexService<ArticleDetailModel> articleIndexService;
    private final StarService<TagStarModel> tagStarService;
    private final ArticlePaidReadService articlePaidReadService;

    public ArticleSearchController(ArticleSearchService searchService, ArticleIndexServiceImpl articleIndexService, TagStarServiceImpl tagStarService, ArticlePaidReadService articlePaidReadService) {
        this.searchService = searchService;
        this.articleIndexService = articleIndexService;
        this.tagStarService = tagStarService;
        this.articlePaidReadService = articlePaidReadService;
    }

    @GetMapping("/list")
    public ResultModel list(UserInfoModel userModel, @RequestParam(required = false,name = "user_id") Long userId, @RequestParam(required = false) String type, @RequestParam(required = false , name = "column_id") Long columnId, @RequestParam(required = false , name = "tag_id") Long tagId, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) {
        Long searchUserId = userModel == null ? null : userModel.getId();
        PageModel<ArticleDetailModel> data = null;
        if(type == null) type = "";
        Map<String,String > order;
        switch (type){
            case "hot":
                order = new HashMap<>();
                order.put("created_time","desc");
                order.put("support","desc");
                order.put("comment_count","desc");
                order.put("view","desc");
                order.put("star_count","desc");
                data = searchService.page(null,null,columnId,userId,searchUserId,null,false,false,false,true,
                        tagId != null ? new ArrayList<Long>(){{ add(tagId);}} : null,null,null,50,null,2,null,null,null,null,null,null,null,null,order,offset,limit);
                break;
            case "star":
                if(userModel == null) return new ResultModel(401,"未登陆");
                List<TagStarDetailModel> listTag = (List<TagStarDetailModel>) tagStarService.search(null,userModel.getId(),null,null,null,null,null,null);
                if(listTag.size() < 1) return new ResultModel(404,"尚未关注标签");
                List<Long> tags = new ArrayList<>();
                for (TagStarDetailModel tagModel: listTag) {
                    tags.add(tagModel.getTagId());
                }
                order = new HashMap<>();
                order.put("created_time","desc");
                order.put("support","desc");
                order.put("comment_count","desc");
                order.put("view","desc");
                order.put("star_count","desc");
                data = searchService.page(null,null,columnId,userId,searchUserId,null,false,false,false,true,tags.size() > 0 ? tags : null,null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);
                break;
            default:
                order = new HashMap<>();
                order.put("created_time","desc");
                data = searchService.page(null,null,columnId,userId,searchUserId,null,false,false,false,true,
                        tagId != null ? new ArrayList<Long>(){{ add(tagId);}} : null,
                        null,null,null,null,null,null,null,null,null,null,null,null,null,order,offset,limit);

        }
        return new ResultModel(data);
    }

    @GetMapping("/{id:\\d+}")
    public ResultModel detail(UserInfoModel userModel, @PathVariable Long id){
        Long searchUserId = userModel == null ? null : userModel.getId();
        ArticleDetailModel detailModel = searchService.getDetail(id,(Long)null,searchUserId);
        if(detailModel == null || detailModel.isDeleted() || detailModel.isDisabled()) return  new ResultModel(404,"错误的编号");

        if(articlePaidReadService.checkRead(detailModel.getPaidRead(),detailModel.getCreatedUserId(),searchUserId) == false){
            String content =  HtmlUtils.subEditorContent(detailModel.getContent(),detailModel.getHtml(),detailModel.getPaidRead().getFreeReadScale());
            detailModel.setContent(content);
            detailModel.setHtml(null);
        }else{
            detailModel.setPaidRead(null);
        }

        return new ResultModel(detailModel);
    }

    @GetMapping("/related")
    public ResultModel related(UserInfoModel userModel, @RequestParam @NotNull Long id, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        Long searchId = userModel == null ? null : userModel.getId();
        ArticleDetailModel detailModel = searchService.getDetail(id);
        if(detailModel == null) return new ResultModel(404,"未知的提问");
        try {
            String[] fields = {"title", "content"};
            IndexSearcher searcher = new IndexSearcher(articleIndexService.getIndexReader());
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
                ArticleIndexModel docData = (ArticleIndexModel) articleIndexService.getModel(document);
                ids.add(docData.getId());
            }
            Integer total = topDocs.scoreDocs.length;
            total = total > maxLimit ? maxLimit : total;
            List<ArticleDetailModel> listData = new ArrayList<>();
            if(!ids.isEmpty()){
                listData = searchService.search(ids,null,null,null,searchId,null,null,null,null,null,null,null);
            }
            PageModel pageModel = new PageModel(offset,limit,total,listData);
            return new ResultModel(pageModel);
        }catch (Exception e){
            return new ResultModel(500,e.getMessage());
        }
    }
}
