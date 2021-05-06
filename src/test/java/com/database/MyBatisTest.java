package com.database;

import com.database.mapper.UserMapper;
import com.database.module.MyBatisUser;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 10, 2020
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
//@ExtendWith(SpringExtension.class)
class MyBatisTest {


    @Autowired
    private UserMapper userMapper;

    @Test
    @org.junit.Test
    void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<MyBatisUser> userList = userMapper.selectList(null);
        Assertions.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }
}
