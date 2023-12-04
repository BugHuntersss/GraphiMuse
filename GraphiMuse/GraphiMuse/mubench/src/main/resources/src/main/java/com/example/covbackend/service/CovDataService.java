package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.CovData;

import java.util.Map;
/**
 * CovData的Service接口，所有查询返回结果都不会是null
 */
public interface CovDataService extends IService<CovData> {

    //通过locationId名字获取全部数据
    Map<String, Object> getDataByLocationIdWithCache(int locationId);
    Map<String, Object> getDataByLocationNameAndTypeWithCache(String name, String type);
    void clearCache();
    void refreshCache();

    //通过locationId获取单个地方数据
    Map<String, Object> getDataByLocationId(int location);
}
