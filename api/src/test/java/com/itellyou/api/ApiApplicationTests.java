package com.itellyou.api;

import com.itellyou.model.sys.EntityType;
import com.itellyou.util.Params;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootTest
class ApiApplicationTests {

    @Test
    void contextLoads() {
        Params params = new Params(new HashMap<String,Object>(){{
            put("id",123);
            put("type","article");
            put("order","desc");
            put("order1","descend");
            put("ip","127.0.0.1");
            put("date","2019-02-07");
            put("datetime","2019-02-07T00:00:13");
        }});

        Integer id = params.getInteger("id");
        assert id.equals(123);

        EntityType type = params.get("type", EntityType.class);
        assert type.equals(EntityType.ARTICLE);
        type = params.getOrDefault("entity", EntityType.class,"answer");
        assert type.equals(EntityType.ANSWER);

        String order = params.get("order", Arrays.asList("desc","asc")).value();
        assert order.equals("desc");
        order = params.getOrDefault("order", Arrays.asList("asc","descend"),"test").value();
        assert order.equals("test");

        order = params.getOrDefault("order1", new HashMap<String,String>(){{
            put("ascend","asc");
            put("descend","desc");
        }},"test").value();
        assert order.equals("desc");

        type = params.get("type",Arrays.asList(EntityType.ANSWER, EntityType.ARTICLE),EntityType.class).value();
        assert type.equals(EntityType.ARTICLE);

        type = params.getOrDefault("type",Arrays.asList(EntityType.ANSWER, EntityType.SOFTWARE),EntityType.class,EntityType.QUESTION).value();
        assert type.equals(EntityType.QUESTION);

        Long ip = params.get("ip",Params.IPLong.class).value();
        assert ip.equals(2130706433l);
        ip = params.getOrDefault("ip1",Params.IPLong.class,0l).value();
        assert ip.equals(0l);
        Long date = params.get("date",Params.Timestamp.class).value();
        assert date.equals(1549468800l);
        date = params.getOrDefault("date1",Params.Timestamp.class,0).value();
        assert date.equals(0l);

        Long datetime = params.get("datetime",Params.Timestamp.class).value();
        assert datetime.equals(1549468813l);
        datetime = params.getOrDefault("datetime1",Params.Timestamp.class,0).value();
        assert datetime.equals(0l);
    }

}
