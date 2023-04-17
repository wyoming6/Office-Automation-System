package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test; //不要引入org.junit.Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestMpDemo1 {
    @Autowired
    private SysRoleMapper mapper;

    @Test
    public void getAll(){
        List<SysRole> list = mapper.selectList(null);
        System.out.println(list);
    }

    @Test
    public void add(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setRoleCode("role");
        sysRole.setDescription("角色管理员");
        int rows = mapper.insert(sysRole);
        System.out.println(sysRole);

    }

    @Test
    public void update(){
        SysRole sysRole = mapper.selectById(10);
        sysRole.setRoleName("角色管理员啊");
        int i = mapper.updateById(sysRole);
        System.out.println(i);
    }

    @Test
    public void deleteById(){
        int i1 = mapper.deleteById(10);
        System.out.println(i1);
    }

    //条件查询
    @Test
    public void testQuery1(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name", "管理员");
        List<SysRole> sysRoles = mapper.selectList(wrapper);
        System.out.println(sysRoles);
    }

    //条件查询
    //这样写的好处是，不需要关注数据库表字段名称是什么
    @Test
    public void testQuery2(){
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, "管理员");
        List<SysRole> sysRoles = mapper.selectList(wrapper);
        System.out.println(sysRoles);
    }
}
