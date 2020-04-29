package com.itellyou.dao.github;

import com.itellyou.model.github.GithubConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GithubConfigDao {

    @Select("select * from github_config limit 0,1")
    GithubConfigModel get();
}
