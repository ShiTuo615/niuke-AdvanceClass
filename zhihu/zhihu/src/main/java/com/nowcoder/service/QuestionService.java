package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question){
        //敏感词过滤，用前缀树记录敏感词，3个指针
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        //加上这个，html脚本就自动被过滤为转译的字符，不会变成脚本，影响页面，一定要做
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        return questionDAO.addQuestion(question);
    }

    public List<Question> selectLastestQuestions(int userId, int offset, int limit){
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }

    public Question selectById(int id){
        return questionDAO.selectById(id);
    }

    public int updateCommentCount(int id, int commentCount){ return questionDAO.upadateCommentCount(id,commentCount);}
}
