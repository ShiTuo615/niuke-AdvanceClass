package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ZhihuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path={"/question/{qid}"}, method ={RequestMethod.GET})
    public String QuestionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionService.selectById(qid);
        model.addAttribute("question",question);

        List<Comment> commentList = commentService.selectCommentByEntity(qid, EntityType.ENTITY_QUESTION, 0,10);
        List<ViewObject> comments = new ArrayList<>();
        for(Comment comment: commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if(hostHolder.getUser()==null){
                vo.set("liked", 0);
            } else{
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user",userService.selectById(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments", comments);

        List<ViewObject> followUsers = new ArrayList<>();
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION,qid,20);
        for(Integer userId : users){
            ViewObject vo = new ViewObject();
            User user = userService.selectById(userId);
            if(user == null){
                continue;
            }
            vo.set("name",user.getName());
            vo.set("headUrl", user.getHeadUrl());
            vo.set("id", user.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid));
        }else{
            model.addAttribute("followed", false);
        }
        return "detail";
    }

    @RequestMapping(path={"/question/add"}, method ={RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            if(hostHolder.getUser() == null){
                //question.setUserId(ZhihuUtil.ANONYMOUS_USERID);
                return ZhihuUtil.getJSONString(999);//前端popup, home 直接返回登陆页面
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question)>0){
//                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
//                        .setActorId(question.getUserId()).setEntityId(question.getId())
//                        .setExts("title", question.getTitle()).setExts("content", question.getContent()));
                return ZhihuUtil.getJSONString(0);
            }
        }catch(Exception e){
            logger.error("增加题目失败"+e.toString());
        }
        return ZhihuUtil.getJSONString(1,"失败");
    }
}
