package com.nowcoder.async.Handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ZhihuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(ZhihuUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.selectById(eventModel.getActorId());

        if(eventModel.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent("用户"+user.getName()+"关注了你的问题，http://127.0.0.1:6080/question/"
                    +eventModel.getEntityId());
        }else if(eventModel.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("用户"+user.getName()+"关注了你，http://127.0.0.1:6080/user/"
                    +eventModel.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
