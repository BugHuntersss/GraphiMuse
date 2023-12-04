package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.Knowledge;
import com.example.covbackend.entity.Location;
import com.example.covbackend.mapper.KnowledgeMapper;
import com.example.covbackend.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService {
    @Autowired
    KnowledgeMapper knowledgeMapper;

    @Override
    public List<Map<String,Object>> getAllKnowledge() {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "title", "description", "imgurl");
        List<Map<String,Object>> maps = listMaps(queryWrapper);
        if(maps == null)
            maps = new ArrayList<>();
        return maps;
    }

    @Override
    public Knowledge getKnowledgeById(int id){
        return getById(id);
    }
}
