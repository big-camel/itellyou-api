package com.itellyou.service.column.impl;

import com.itellyou.dao.column.ColumnMemberDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.column.ColumnDetailModel;
import com.itellyou.model.column.ColumnMemberDetailModel;
import com.itellyou.model.column.ColumnMemberModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.service.column.ColumnMemberService;
import com.itellyou.service.column.ColumnSearchService;
import com.itellyou.service.user.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
public class ColumnMemberServiceImpl implements ColumnMemberService {

    private final ColumnMemberDao memberDao;
    private final ColumnSearchService columnSearchService;
    private final UserSearchService userSearchService;

    @Autowired
    public ColumnMemberServiceImpl(ColumnMemberDao memberDao,ColumnSearchService columnSearchService,UserSearchService userSearchService){
        this.memberDao = memberDao;
        this.columnSearchService = columnSearchService;
        this.userSearchService = userSearchService;
    }

    @Override
    public int insert(ColumnMemberModel model) throws Exception {
        return memberDao.insert(model);
    }

    @Override
    public int delete(Long columnId, Long userId) throws Exception {
        return memberDao.delete(columnId,userId);
    }

    @Override
    public List<ColumnMemberDetailModel> search(Long columnId, Long userId,Long searchId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<ColumnMemberDetailModel> detailModels = memberDao.search(columnId,userId,beginTime,endTime,ip,order,offset,limit);
        HashSet<Long> userHash = new LinkedHashSet<>();
        HashSet<Long> columnHash = new LinkedHashSet<>();
        for (ColumnMemberDetailModel detailModel : detailModels){
            if(!userHash.contains(detailModel.getUserId())){
                userHash.add(detailModel.getUserId());
            }
            if(!columnHash.contains(detailModel.getColumnId())){
                columnHash.add(detailModel.getColumnId());
            }
        }
        List<UserDetailModel> userDetailModels = userSearchService.search(userHash,searchId,null,null,null,null,null,null,null,null,null,null);
        List<ColumnDetailModel> columnDetailModels = columnSearchService.search(columnHash,null,null,null,searchId,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        for (ColumnMemberDetailModel detailModel : detailModels){
            for (UserDetailModel user : userDetailModels){
                if(user.getId().equals(detailModel.getUserId())){
                    detailModel.setUser(user);
                    break;
                }
            }
            for (ColumnDetailModel column : columnDetailModels){
                if(column.getId().equals(detailModel.getColumnId())){
                    detailModel.setColumn(column);
                    break;
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Long columnId, Long userId, Long beginTime, Long endTime, Long ip) {
        return memberDao.count(columnId,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<ColumnMemberDetailModel> page(Long columnId, Long userId,Long searchId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;

        List<ColumnMemberDetailModel> data = search(columnId,userId,searchId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(columnId,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }
}
