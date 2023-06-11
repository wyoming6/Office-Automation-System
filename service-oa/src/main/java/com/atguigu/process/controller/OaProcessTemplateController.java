package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService processTemplateService;

    /**
     * 分页查询审批模板
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit) {
        Page<ProcessTemplate> pageParam = new Page<>(page,limit);
        //根据oa_process_template表中的process_type_id字段，查询oa_process_type表中的name字段
        IPage<ProcessTemplate> pageModel =
                processTemplateService.selectPageProcessTempate(pageParam);
        return Result.ok(pageModel);
    }

    /**
     * 查询
     * @param id
     * @return
     */
    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    /**
     * 保存
     * @param processTemplate
     * @return
     */
    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    /**
     * 修改
     * @param processTemplate
     * @return
     */
    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }

    /**
     * 上传流程定义
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //获取classes目录位置
        String path = new File(ResourceUtils.getURL("classpath:")
                .getPath()).getAbsolutePath();

        //设置上传文件夹
        File tempFile = new File(path + "/processes/");
        if(!tempFile.exists()) {
            tempFile.mkdirs();
        }
        //创建空文件，实现文件写入
        String filename = file.getOriginalFilename();
        File zipFile = new File(path + "/processes/" + filename);

        //保存文件
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }

        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，设置文件名称为流程定义的key
        map.put("processDefinitionPath", "processes/" + filename);
        map.put("processDefinitionKey", filename.substring(0, filename.lastIndexOf(".")));
        return Result.ok(map);
    }

    /**
     * 部署流程定义（发布）
     * @param id
     * @return
     */
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        //流程定义部署
        processTemplateService.publish(id);
        return Result.ok();
    }

    public static void main(String[] args) {
        try {
            String path = new File(ResourceUtils.getURL("classpath:")
                    .getPath()).getAbsolutePath();
            System.out.println("getURL:");
            System.out.println(ResourceUtils.getURL("classpath:"));
            System.out.println("getPath:");
            System.out.println(ResourceUtils.getURL("classpath:")
                    .getPath());
            System.out.println("AbsolutePath:");
            System.out.println(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}

