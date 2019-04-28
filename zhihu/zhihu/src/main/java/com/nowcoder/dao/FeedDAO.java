package com.nowcoder.dao;

import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedDAO {
    String TABLE_NAME = "feed";
    String SELECT_FIELDS = " id, created_date, user_id, data, type ";
    String INSERT_FIELDS = " created_date, user_id, data, type ";

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{createdDate},#{userId},#{data},#{type})"})
    int addFeed(Feed feed);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
