package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.Train;

import java.util.HashMap;
import java.util.List;

public interface TrainService extends IService<Train> {
    List<HashMap<String, Object>> getByKeyword(String keyword, String from , String to);
    List<HashMap<String, Object>> getByFromAndTo(String from, String to);
}
