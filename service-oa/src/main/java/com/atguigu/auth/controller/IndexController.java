package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    //login
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        //{"code":200, "data":{"token":"admin-token"}}
//        Map<String,Object> map = new HashMap<>();
//        map.put("token", "admin-token");
//        return Result.ok(map);

        //获取用户名和密码，查询数据库
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(wrapper);

        //判断用户是否存在
        if(sysUser == null){
            throw new GuiguException(201, "用户不存在");
        }

        //判断密码是否正确
        String passwordDB = sysUser.getPassword(); //数据库中的密码
        String passwordInput = MD5.encrypt(loginVo.getPassword()); //输入的密码
        if(!passwordDB.equals(passwordInput)){
            throw new GuiguException(201,"密码错误");
        }

        //判断用户是否被禁用
        if(sysUser.getStatus().intValue() == 0){
            throw new GuiguException(201,"用户已被禁用");
        }

        //根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        //将token放入请求头，返回
        Map<String,Object> map = new HashMap<>();
        map.put("token", token);
        return Result.ok(map);


    }

    //info
    @GetMapping("info")
    public Result info(HttpServletRequest request){
        // 从请求头获取用户信息（token字符串）
        String token = request.getHeader("token");

        //从token中获取用户id、用户名称
        Long userId = JwtHelper.getUserId(token);

        //从数据库提取用户
        SysUser sysUser = sysUserService.getById(userId);

        //用户可操作的菜单
        List<RouterVo> routers = sysMenuService.findMenusByUserID(userId);

        //用户可操作的按钮
        List<String> perms = sysMenuService.findPermsByUserID(userId);

        //返回
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("routers", routers);
        map.put("buttons", perms);
        return Result.ok(map);
    }

    //退出
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
