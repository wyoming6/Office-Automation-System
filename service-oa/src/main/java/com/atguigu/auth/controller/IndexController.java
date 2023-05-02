package com.atguigu.auth.controller;

import com.atguigu.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    //login
    @PostMapping("login")
    public Result login(){
        //{"code":200, "data":{"token":"admin-token"}}
        Map<String,Object> map = new HashMap<>();
        map.put("token", "admin-token");
        return Result.ok(map);
    }

    //info
    @GetMapping("info")
    public Result info(){
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name","admin");
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        return Result.ok(map);
    }

    //退出
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
