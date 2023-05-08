package com.atguigu.auth.service;

import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    //查询所有角色和当前用户的角色
    Map<String, Object> findRoleDataByUserId(Long userId);
    //为用户分配角色（一个用户可能有多个角色）
    void doAssign(AssignRoleVo assignRoleVo);
}
