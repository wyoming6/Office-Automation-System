package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    //查询所有角色
    // http://localhost:8800/admin/system/sysRole/findAll
//    @GetMapping("/findAll")
//    public List<SysRole> findAll(){
//        List<SysRole> list = sysRoleService.list();
//        return list;
//    }

    //return all roles
    @GetMapping("/findAll")
    public Result findAll(){
        List<SysRole> list = sysRoleService.list();
        try{
            int i = 10/0;
        }catch(Exception e){
            throw new GuiguException(20001, "执行自定义异常处理");
        }

        return Result.ok(list);
    }


    /**
     * 带条件的分页查询
     * @param page 当前页
     * @param limit 每页记录数
     * @param sysRoleQueryVo 条件对象
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> sysRolePage = new Page<>(page, limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName, roleName);
        }
        IPage<SysRole> page1 = sysRoleService.page(sysRolePage, wrapper);
        return Result.ok(page1);
    }

    /**
     * 添加角色
     * @param role 角色
     * @return
     */
    //以下这种方式也可以：
    //public Result save(SysRole role)
    //@GetMapping("save")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role){
        boolean isSaved = sysRoleService.save(role);
        if(isSaved){
            return Result.ok();
        }
        return Result.fail();
    }

    /**
     * 查询角色
     * @param id 角色id
     * @return
     */
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysRole byId = sysRoleService.getById(id);
        return Result.ok(byId);
    }

    /**
     * 修改角色（最终修改）
     * @param role 角色
     * @return
     */
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        boolean isSuccessful = sysRoleService.updateById(role);
        if(isSuccessful){
            return Result.ok();
        }
        return Result.fail();
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean isSuccessful = sysRoleService.removeById(id);
        if(isSuccessful){
            return Result.ok();
        }
        return Result.fail();

    }

    /**
     * 批量删除
     * @param idList
     * @return
     */
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean isSuccessful = sysRoleService.removeByIds(idList);
        if(isSuccessful){
            return Result.ok();
        }
        return Result.fail();
    }

}
