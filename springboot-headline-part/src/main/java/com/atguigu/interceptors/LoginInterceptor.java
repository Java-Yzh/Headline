package com.atguigu.interceptors;

import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登陆包含拦截器，检查请求头中是否包含有效token
 *      有，有效，放行
 *      没有，无效，返回504
 *
 *      author : 尹子豪
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtHelper jwtHelper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中获取token
        String token = request.getHeader("token");
        //检查是否有效
        boolean expiration = jwtHelper.isExpiration(token);
        //有效则放行
        if (!expiration){
            //表示token没过期，放行
            return true;
        }
        //无效则返回504的状态json
        Result result = Result.build(null, ResultCodeEnum.NOTLOGIN);
        //将result转换为json
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().println(json);

        return false;
    }
}
