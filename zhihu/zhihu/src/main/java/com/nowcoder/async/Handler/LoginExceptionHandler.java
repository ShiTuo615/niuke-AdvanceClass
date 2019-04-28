package com.nowcoder.async.Handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandler(EventModel eventModel) {
        //判断是否有异常登录
        Message message = new Message();
        message.setToId(eventModel.getActorId());
        message.setContent("你上次的登录ip异常");
        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId(message.getConversationId());
        messageService.addMessage(message);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("username",eventModel.getExts("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExts("email"),"登录异常","mails/login_exception.html",map);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LOGIN);
    }
}
