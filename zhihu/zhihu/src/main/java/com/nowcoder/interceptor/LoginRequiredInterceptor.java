package com.nowcoder.interceptor;

import com.nowcoder.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//试着看在单一页面使用拦截器，这里是user页面只能在登陆时才能访问，没登陆会自动跳转到登陆页面登陆，登陆后会跳转回来
//实现方法，用next值记录下来当前访问页面，跳转登陆页面后再用next的值返回
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(hostHolder.getUser() == null){
            response.sendRedirect("/reglogin/?next=" + request.getRequestURI());//getRequestURI记录下当前访问的页面
            return false;//返回false就不会继续进入controller了
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
