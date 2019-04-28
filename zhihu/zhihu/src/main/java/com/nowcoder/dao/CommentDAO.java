package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String SELECT_FIELDS = " id, content, user_id, entity_id, entity_type, created_date, status ";
    String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status ";

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addComment(Comment comment);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Comment selectById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc limit #{offset},#{limit}"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType,
                                        @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);

    @Update({"update comment set status=#{status} where id=#{id}"})
    int updateStatus(@Param("status") int status, @Param("id") int id);
}
