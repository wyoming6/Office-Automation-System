package com.atguigu.security.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.ResponseUtil;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
//    private RedisTemplate redisTemplate;
//    public TokenAuthenticationFilter(RedisTemplate redisTemplate){
//        this.redisTemplate = redisTemplate;
//    }

    public TokenAuthenticationFilter(){
    }

    /**
     * 判断是否登录
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    /**
     * 从请求中获取token
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");

        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            if (!StringUtils.isEmpty(username)) {
                return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            }

//            if (!StringUtils.isEmpty(username)) {
//                //从redis获取权限数据
//                String authString = (String) redisTemplate.opsForValue().get(username);
//                //把权限数据由字符串转为集合List<SimpleGrantedAuthority>
//                if(!StringUtils.isEmpty(authString)){
//                    List<Map> mapList = JSON.parseArray(authString, Map.class);
//                    System.out.println("检测:\n");
//                    System.out.println(mapList);
//                    List<SimpleGrantedAuthority> authList = new ArrayList<>();
//                    for (Map map : mapList) {
//                        authList.add(new SimpleGrantedAuthority((String)map.get("authority")));
//                    }
//                    return new UsernamePasswordAuthenticationToken(username, null, authList);
//                }else{
//                    return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
//                }
//            }
        }
        return null;
    }
}
