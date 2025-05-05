package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * url要和controller方法处理完请求之后，响应信息返回的页面的资源目录保持一致
     */
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){
        //请求转发到登录页面
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    public @ResponseBody Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request , HttpServletResponse response, HttpSession session){

        /**
         * 在前面，我们用到最多的是控制器返回页面的路径，这里，使用了 SpringMVC 中的 @ResponseBody 注解
         * 表明该方法的返回值作为 HTTP 响应体（Response Body）返回给前端，该方法的返回值 returnObject 会默认转成 json 返回给前端
         */

        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        //调用service层方法，查询用户
        User user=userService.queryUserByLoginActAndPwd(map);

        /**
         * ReturnObject 对象用于根据查询结果，生成响应信息
         * 其实完全可以将代表能否正常登录的 Code 和 记录错误信息的 Message 封装到 Map 中，那为什么还要自己定义一个 ReturnObject 对象
         * 这是因为 Map 效率比较低（跟 Map 的底层实现有关），而能够被转成 json 字符串（返回给前端）的，除了 Map，还有实体类
         * 实体类效率比 Map 高，所以我们自己写了一个 ReturnObject 类
         */
        ReturnObject returnObject=new ReturnObject();

        if(user==null){
            //登录失败,用户名或者密码错误
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或者密码错误");
        }else{
            //进一步判断账号是否合法
            String nowStr=DateUtils.formateDateTime(new Date());
            if(nowStr.compareTo(user.getExpireTime())>0){
                //登录失败，账号已过期
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已过期");
            }else if("0".equals(user.getLockState())){
                //登录失败，状态被锁定
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
                //登录失败，ip受限
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("ip受限");
            }else{
                //登录成功
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);

                // 把登录用户的信息保存到 Session 域中
                session.setAttribute(Contants.SESSION_USER,user);

                // 如果需要记住密码，则需要向前端页面返回 Cookie
                if("true".equals(isRemPwd)){
                    Cookie c1=new Cookie("loginAct",user.getLoginAct());
                    c1.setMaxAge(60*60*24*10); // 生存周期

                    // 把 Cookie 存到 HTTP 响应头中，浏览器收到响应后，就会自动保存这个 Cookie
                    response.addCookie(c1);
                    Cookie c2=new Cookie("loginPwd",user.getLoginPwd());
                    c2.setMaxAge(60*60*24*10);
                    response.addCookie(c2);
                }else{
                    // 把没有过期的 cookie 删除
                    Cookie c1=new Cookie("loginAct","1");
                    c1.setMaxAge(0);
                    response.addCookie(c1);
                    Cookie c2=new Cookie("loginPwd","0");
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }
            }
        }

        return returnObject;
    }

    @RequestMapping("settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session){
        // 清空 Cookie
        Cookie c1=new Cookie("loginAct","1");
        c1.setMaxAge(0);
        response.addCookie(c1);
        Cookie c2=new Cookie("loginPwd","0");
        c2.setMaxAge(0);
        response.addCookie(c2);

        // 销毁 Session
        session.invalidate();

        // 通过重定向的方式，让浏览器重新发送一次请求，去访问项目的根 url
        return "redirect:/";
    }
}
