package com.itellyou.service.sys;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysLinkModel;

import java.util.List;
import java.util.Map;

public interface SysLinkService {

    int insert(SysLinkModel mode);

    int delete(Long id);

    List<SysLinkModel> search(Long id,
                              String text,
                              String link,
                              String target,
                              Long userId,
                              Long beginTime, Long endTime,
                              Long ip,
                              Map<String, String> order,
                              Integer offset,
                              Integer limit);

    int count(Long id,
              String text,
              String link,
              String target,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysLinkModel> page(Long id,
                                 String text,
                                 String link,
                                 String target,
                                 Long userId,
                                 Long beginTime, Long endTime,
                                 Long ip,
                                 Map<String, String> order,
                                 Integer offset,
                                 Integer limit);
}
