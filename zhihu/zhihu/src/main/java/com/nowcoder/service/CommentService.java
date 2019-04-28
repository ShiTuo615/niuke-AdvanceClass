package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    public void addComment(Comment comment){
        commentDAO.addComment(comment);
    }

    public Comment selectById(int id){
        return commentDAO.selectById(id);
    }

    public List<Comment> selectCommentByEntity(int entityId, int entityType, int offset, int limit){
        return commentDAO.selectCommentByEntity(entityId, entityType, offset, limit);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    public int deleteComemntById(int status, int id){
        return commentDAO.updateStatus(1,id);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }
}
