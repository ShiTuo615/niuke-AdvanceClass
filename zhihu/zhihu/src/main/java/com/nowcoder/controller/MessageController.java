package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ZhihuUtil;
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
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @RequestMapping(path={"/msg/list"},method ={RequestMethod.GET})
    public String getConversationList(Model model){
        if(hostHolder.getUser()==null){
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationList = messageService.getConversationList(localUserId,0,10);
        List<ViewObject> conversations = new ArrayList<>();
        for(Message message: conversationList){
            ViewObject vo = new ViewObject();
            vo.set("message", message);
            int targetId = message.getFromId() == localUserId? message.getToId() : message.getFromId();
            vo.set("user",userService.selectById(targetId));
            vo.set("unread", messageService.getConvesationUnreadCount(localUserId,message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    @RequestMapping(path={"/msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @RequestParam("conversationId") String conversationId){
        try{
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message: messageList){
                if(message.getToId() == hostHolder.getUser().getId()){
                    messageService.updateHasread(message.getId(),1);
                }
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.selectById(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情失败"+e.toString());
        }
        return "letterDetail";
    }

    @RequestMapping(path={"/msg/addMessage"},method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try{
            if(hostHolder.getUser() == null){
                return ZhihuUtil.getJSONString(999,"未登录");
            }

            User user = userService.selectByName(toName);
            if(user == null){
                return ZhihuUtil.getJSONString(1,"用户不存在");
            }
            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);
            return ZhihuUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("发送消息失败"+e.toString());
            return ZhihuUtil.getJSONString(1,"发信失败");
        }
    }
}
