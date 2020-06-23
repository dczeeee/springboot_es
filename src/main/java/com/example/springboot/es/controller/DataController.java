package com.example.springboot.es.controller;

import com.example.springboot.es.entity.es.EsBlog;
import com.example.springboot.es.entity.mysql.MysqlBlog;
import com.example.springboot.es.repository.es.EsBlogRepository;
import com.example.springboot.es.repository.mysql.MysqlBlogRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class DataController {

    @Autowired
    private MysqlBlogRepository mysqlBlogRepository;

    @Autowired
    private EsBlogRepository esBlogRepository;

    @GetMapping("/blogs")
    public Object blog(){
        List<MysqlBlog> mysqlBlogs = mysqlBlogRepository.queryAll();
        return mysqlBlogs;
    }

    @PostMapping("/search")
    public Object search(@RequestBody Param param){
        Map<String, Object> map = new HashMap<>();
        String type = param.getType();
        StopWatch watch = new StopWatch();
        watch.start();
        if(type.equalsIgnoreCase("mysql")){
            List<MysqlBlog> mysqlBlogs = mysqlBlogRepository.queryBlogs(param.getKeyword());
            map.put("list",mysqlBlogs);
        }else if(type.equalsIgnoreCase("es")){
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            builder.should(QueryBuilders.matchPhraseQuery("title",param.getKeyword()));
            builder.should(QueryBuilders.matchPhraseQuery("content",param.getKeyword()));
            String s = builder.toString();
            Page<EsBlog> esBlogs = (Page<EsBlog>) esBlogRepository.search(builder);
            List<EsBlog> content = esBlogs.getContent();
            map.put("list",content);
        }else {
            return ">>> 不知道 <<<";
        }
        watch.stop();
        long totalTimeMillis = watch.getTotalTimeMillis();
        map.put("duration",totalTimeMillis);
        return map;
    }

    @GetMapping("/blog/{id}")
    public Object blog(@PathVariable("id") Integer id) {
        Optional<MysqlBlog> byId = mysqlBlogRepository.findById(id);
        MysqlBlog blog = byId.get();
        return blog;
    }

    @Data
    public static class Param{
        // String,es
        private String type;
        private String keyword;
    }
}
