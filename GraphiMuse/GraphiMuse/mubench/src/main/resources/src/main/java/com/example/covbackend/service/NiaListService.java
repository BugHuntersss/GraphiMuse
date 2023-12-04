package com.example.covbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.covbackend.entity.NiaList;

import java.util.List;

public interface NiaListService extends IService<NiaList> {
    List<NiaList> getByProvince(String province);
    List<NiaList> getByKeyword(String keyword, String province);
    List<NiaList> getPolicyByKeyword(String keyword);
}
