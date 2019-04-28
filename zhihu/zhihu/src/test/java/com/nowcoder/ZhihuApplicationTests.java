package com.nowcoder;

import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.User;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ZhihuUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import sun.security.provider.MD5;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ZhihuApplication.class)
@WebAppConfiguration
public class ZhihuApplicationTests {

    @Autowired
    UserService userService;
    @Autowired
    UserDAO userDAO;
    @Test
    public void contextLoads() {
//        userService.register("st","12345");
//        User user = userDAO.selectByName("st");
//        String password = ZhihuUtil.MD5("12345"+user.getSalt());
//        Assert.assertEquals(user.getPassword(),password);
        for(int i=0;i<11;++i){
            User user = userDAO.selectByName(String.format("USER%d",i));
            user.setPassword(ZhihuUtil.MD5("12345"));
            userDAO.updatePassword(user);
        }
        Assert.assertEquals(ZhihuUtil.MD5("12345"),userDAO.selectById(1).getPassword());
    }

}
