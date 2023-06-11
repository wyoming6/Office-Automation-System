package com.atguigu.process.service;

import com.atguigu.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface OaProcessTypeService extends IService<ProcessType> {
    //查询所有审批分类，以及每个审批分类下的审批模板
    List<ProcessType> findProcessType();
}
