package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        List<User> userList=userService.queryAllUsers();
        request.setAttribute("userList", userList);
        return "/workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    public @ResponseBody Object saveCreateActivity(Activity activity, HttpSession session){

        /**
         * 这里为什么可以从 session 域中取到 user？
         * 因为在用户登录成功时，UserController 把用户信息写到 session 域中了！
         * 不是前端把用户信息写到 session 域中的
         */
        User user=(User)session.getAttribute(Contants.SESSION_USER);

        /**
         * 这里要注意，创建市场活动的模态窗口中只有 6 个填写项，但是 Mapper 的插入语句中要插入 9 个字段
         * 所以模态窗口中没有的 3 个字段我们要通过下面的代码进行赋值
         */
        activity.setId(UUIDUtils.getUUID()); // UUIDUtils.getUUID() 是我们自己写的一个自动生成 id 的工具，很简单，没什么好说的
        activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());

        // 调用业务层方法，保存创建的市场活动

        // 返回值封装到实体类 ReturnObject 中
        ReturnObject returnObject=new ReturnObject();

        // 保存市场活动属于往数据库中写数据，写数据要考虑有没有异常；查数据不考虑异常，所以这里用 try-catch
        try{
            int ret= activityService.saveCreateActivity(activity);
            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);

                /**
                 * 这个创建失败属于是业务型的失败，比如某些字段不符合要求、插入的内容重复等等
                 */
                returnObject.setMessage("创建失败...");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);

            /**
             * 这个创建失败属于是技术型失败或数据库异常，比如 SQL 语法错误、数据库断连等等
             */
            returnObject.setMessage("创建失败...");
        }

        return returnObject;
    }
}
