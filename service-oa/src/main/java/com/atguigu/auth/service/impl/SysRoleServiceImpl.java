package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    /**
     * 查询所有角色和当前用户的角色
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        //查询所有的角色
        List<SysRole> allRolesList = baseMapper.selectList(null);

        //查询user id对应的所有角色的id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);
        List<Long> existRoleIDList = existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //根据角色id找到对应角色的信息
        List<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole role : allRolesList) {
            if(existRoleIDList.contains(role.getId())) {
                assignRoleList.add(role);
            }
        }

        //封装数据到Map中
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList", assignRoleList);
        roleMap.put("allRolesList", allRolesList);
        return roleMap;
    }

    /**
     * 为用户分配角色（一个用户可能有多个角色）
     * @param assignRoleVo
     */
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        //删除用户的旧角色
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

        //给用户分配新角色
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        for(Long roleId : roleIdList) {
            if(StringUtils.isEmpty(roleId)) continue;
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }

    }
}
