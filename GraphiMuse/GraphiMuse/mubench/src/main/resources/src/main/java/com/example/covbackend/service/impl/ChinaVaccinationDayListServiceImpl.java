package com.example.covbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.covbackend.entity.ChinaVaccinationDayList;
import com.example.covbackend.mapper.ChinaVaccinationDayListMapper;
import com.example.covbackend.service.ChinaVaccinationDayListService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChinaVaccinationDayListServiceImpl extends ServiceImpl<ChinaVaccinationDayListMapper, ChinaVaccinationDayList> implements ChinaVaccinationDayListService {
    @Override
    public List<Map<String, Object>> getAllChinaVaccinationDayList() {
        List<Map<String,Object>> maps = listMaps();
        if(maps == null)
            maps = new ArrayList<>();
        return maps;
    }
}
