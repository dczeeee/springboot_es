package com.example.springboot.es.controller;

import com.example.springboot.es.entity.mysql.MysqlBlog;
import com.example.springboot.es.repository.mysql.MysqlBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    MysqlBlogRepository mysqlBlogRepository;

    @RequestMapping("/")
    public String index(){
        List<MysqlBlog> all = mysqlBlogRepository.findAll();
        return "index.html";
    }
}
