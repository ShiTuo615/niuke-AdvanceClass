package com.nowcoder.async.Handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;

    private String buildFeedData(EventModel model){
        Map<String, String> map = new HashMap<String, String>();
        User actor = userService.selectById(model.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if(model.getEventType() == EventType.COMMENT ||
                (model.getEventType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.selectById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
        }

    @Override
    public void doHandler(EventModel eventModel) {
        //为了测试，动作发出者是个1-10之间的随机数
        Random r = new Random();
        eventModel.setActorId(1+r.nextInt(10));
        //构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getEventType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        if(feed.getData() == null){
            //不支持的feed
            return;
        }
        feedService.addFeed(feed);

        //获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,
                eventModel.getActorId(), Integer.MAX_VALUE);
        //系统队列,没登录也可以看到
        followers.add(0);
        //给所有粉丝推事件
        for(int follower : followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }
}

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}
