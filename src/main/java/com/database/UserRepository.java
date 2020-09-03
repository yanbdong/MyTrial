package com.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 31, 2020
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByNameLike(String name);

    User readByName(String name);

    List<User> getByCreateDateLessThan(Date star);
}
