package com.baine.toutiao.controller;

import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventProducer;
import com.baine.toutiao.async.EventType;
import com.baine.toutiao.service.NewsService;
import com.baine.toutiao.service.UserService;
import com.baine.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/reg"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String preRegister(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "rember", defaultValue = "0") int rememberMe,
                           HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.registerCheck(username, password);
            if (!map.containsKey("msgname") && !map.containsKey("msgpwd")) {
                // 增加发送邮件的事务
                eventProducer.fireEvent(new EventModel(EventType.REGISTER)
                        .setExt("username", username).setExt("ticket", (String) map.get("ticket"))
                        .setExt("domain", ToutiaoUtil.TOUTIAO_DOMAIN));
                return ToutiaoUtil.getJSONString(0, "预注册成功, 邮件已发送至指定邮箱");
            }
            else
                return ToutiaoUtil.getJSONString(1, map);
        } catch (Exception e) {
            logger.error("预注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "预注册异常");
        }
    }

    @RequestMapping(path = {"/register"}, method = {RequestMethod.GET})
    public String register(@RequestParam("ticket") String ticket, HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(ticket);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            }
            else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberMe,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.loginUser(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberMe > 0) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setActorID((int) map.get("userId")).setExt("username", username).setExt("email", "1597256127@qq.com"));
                return ToutiaoUtil.getJSONString(0, "登陆成功");
            }
            else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登陆异常");
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
