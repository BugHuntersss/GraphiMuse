package com.example.covbackend.controller;

import com.example.covbackend.common.Result;
import com.example.covbackend.entity.Knowledge;
import com.example.covbackend.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RequestMapping("/knowledge")
@RestController
public class KnowledgeController {
    @Resource
    KnowledgeService knowledgeService;

    @RequestMapping("/getallknowledge")
    Result getAllKnowledge(){
        return Result.succ(knowledgeService.getAllKnowledge());
    }

    @RequestMapping("/getknowledge")
    Result getKnowledgeById(@RequestBody Map map) {
        int id = Integer.parseInt((String) map.get("id"));
        Knowledge knowledge = knowledgeService.getKnowledgeById(id);
        if (knowledge == null)
            return Result.fail("没有这条知识");
        else
            return Result.succ(knowledge);
    }
}
