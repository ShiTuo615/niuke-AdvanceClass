package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questionList= questionService.selectLastestQuestions(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for(Question question: questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("type", QuestionEntityType.questionType.get(question.getEntityType()));
            vo.set("user", userService.selectById(question.getUserId()));
            vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            vos.add(vo);
        }
        return vos;
    }
    @RequestMapping(path={"/","/index"}, method ={RequestMethod.GET,RequestMethod.POST})
    public String index(Model model)
    {
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        User user = userService.selectById(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, userId));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }
}
