package com.example.covbackend.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.NiaList;
import com.example.covbackend.mapper.NiaListMapper;
import com.example.covbackend.service.NiaListService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NiaListServiceImpl extends ServiceImpl<NiaListMapper, NiaList> implements NiaListService {

    @Resource
    NiaListMapper niaListMapper;

    public List<NiaList> getByProvince(String province){
        if(province==null){
            province = "北京";
        }

        QueryWrapper<NiaList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("province",province).isNotNull("province");

        return list(queryWrapper);
    }

    public List<NiaList> getByKeyword(String keyword, String province){

        QueryWrapper<NiaList> queryWrapper = new QueryWrapper<>();

        if(province==null){
            queryWrapper.like("name","%"+keyword+"%").isNotNull("province");
        }
        else {
            queryWrapper.like("name","%"+keyword+"%").eq("province",province);
            //System.out.println(list(queryWrapper));
        }

        return list(queryWrapper);
    }

    public List<NiaList> getPolicyByKeyword(String keyword){
        QueryWrapper<NiaList> queryWrapper = new QueryWrapper<>();

        queryWrapper.like("name","%"+keyword+"%").isNull("province");

        return list(queryWrapper);
    }
}
