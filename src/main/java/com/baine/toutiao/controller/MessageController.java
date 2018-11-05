package com.baine.toutiao.controller;

import com.baine.toutiao.model.HostHolder;
import com.baine.toutiao.model.Message;
import com.baine.toutiao.model.User;
import com.baine.toutiao.model.ViewObject;
import com.baine.toutiao.service.MessageService;
import com.baine.toutiao.service.UserService;
import com.baine.toutiao.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(fromId);
            msg.setToId(toId);
            msg.setCreatedDate(new Date());
            msg.setHasRead(0);
            msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId): String.format("%d_%d", toId, fromId));
            messageService.addMessage(msg);
            return ToutiaoUtil.getJSONString(msg.getId());
        } catch (Exception e) {
            logger.error("发送站内信失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发送站内信失败");
        }
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg: conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                vo.set("unreadCount", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                // 可能是我发给别人, 也可能是别人发给我
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                vo.set("userName", user.getName());
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取对话站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId) {
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg: messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
                messageService.updateHasRead(msg.getId(), 1);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letterDetail";
    }
}
