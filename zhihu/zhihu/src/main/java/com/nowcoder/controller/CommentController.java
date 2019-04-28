package com.nowcoder.controller;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.util.ZhihuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            if(hostHolder.getUser() == null){
                comment.setUserId(ZhihuUtil.ANONYMOUS_USERID);
            }else{
                comment.setUserId(hostHolder.getUser().getId());
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            comment.setContent(content);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(questionId,count);

        }catch(Exception e){
            logger.error("发布评论失败"+ e.toString());
        }
        return "redirect:/question/" + questionId;
    }
}
