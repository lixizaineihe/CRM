package com.bjpowernode.crm.settings.web.interceptor;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// 使用 SpringMVC 的拦截器
public class LoginInterceptor implements HandlerInterceptor {

    // 该方法在 Controller 方法执行之前执行
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        // 如果用户没有登录，则跳转到登陆页面

        /**
         * 在写 UserController 的 login 方法时，我们定义了，如果用户登陆成功，是会把用户信息保存到 session 域中的
         * 所以在 CRM 这个项目打开的期间，我们都可以从 session 域中取用户信息，如果用户登录了的话！
         * 所以我们可以以此来判断用户是否是登录的状态
         */
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        if (user == null) {

            // 获取当前项目的根路径，进行重定向，将页面跳转回登陆页面
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath());

            // 拦截器不放行
            return false;
        }

        // 拦截器放行
        return true;
    }

    // 该方法在 Controller 方法执行之后，视图渲染之前执行
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    // 该方法在整个请求处理完成之后，视图渲染之后执行
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
