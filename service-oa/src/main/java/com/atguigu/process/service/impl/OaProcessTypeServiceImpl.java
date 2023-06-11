package com.atguigu.process.service.impl;


import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.mapper.OaProcessTypeMapper;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    /**
     * 查询所有审批分类，以及每个审批分类下的审批模板
     * @return
     */
    @Override
    public List<ProcessType> findProcessType() {
        //查询所有审批分类
        List<ProcessType> processTypeList = baseMapper.selectList(null);

        for (ProcessType processType:processTypeList) {
            Long typeId = processType.getId();
            //根据审批分类id，查询该分类下的审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

            //将审批模板数据封装到审批分类对象中
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
