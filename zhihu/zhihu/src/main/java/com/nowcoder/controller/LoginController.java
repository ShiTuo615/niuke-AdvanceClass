package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="next") String next,
                      @RequestParam(value = "remember_me", defaultValue = "false") boolean remember_me,
                      HttpServletResponse response){
        try{
            Map<String, Object> map = userService.register(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(remember_me){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch(Exception e){
            logger.error("注册异常"+e.toString());
            return "login";
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model,@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value="next", required = false) String next,
                        @RequestParam(value = "remember_me", defaultValue = "false") boolean remember_me,
                        HttpServletResponse response){
        try{
            Map<String, Object> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(remember_me){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                //一登陆就发邮件，提示登陆异常
//                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
//                        .setExts("username", username).setExts("email", "846705130@qq.com")
//                        .setActorId((int)map.get("userId")));
                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch(Exception e){
            logger.error("登录异常"+e.toString());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"},method = {RequestMethod.GET, RequestMethod.POST})
    public String Logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
