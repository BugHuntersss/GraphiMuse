package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Knowledge;

import java.util.List;
import java.util.Map;

public interface KnowledgeService extends IService<Knowledge> {
    List<Map<String,Object>> getAllKnowledge();
    Knowledge getKnowledgeById(int id);
}
