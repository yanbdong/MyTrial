package com.database;

import com.database.module.Department;
import com.database.module.Role;
import com.database.module.User;
import com.database.repo.DepartmentRepository;
import com.database.repo.RoleRepository;
import com.database.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 10, 2020
 */
@SpringBootTest(classes = { JpaConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
@ExtendWith(SpringExtension.class)
class MySqlTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    public void initData() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();

        Department department = new Department();
        department.setName("开发部");
        departmentRepository.save(department);
        Assert.notNull(department.getId());

        Role role = new Role();
        role.setName("admin");
        roleRepository.save(role);
        Assert.notNull(role.getId());

        User user = new User();
        user.setName("user");
        user.setCreateDate(new Date());
        user.setDepartment(department);

        List<Role> roles = roleRepository.findAll();
        Assert.notNull(roles);
        user.setRoles(roles);

        userRepository.save(user);
        Assert.notNull(user.getId());
    }

    @Test
    public void findPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<User> page = userRepository.findAll(pageable);
        Assert.notNull(page);
        for (User user : page.getContent()) {
            log.info("====user==== user name:{}, department name:{}, role name:{}", user.getName(),
                    user.getDepartment().getName(), user.getRoles().get(0).getName());
        }
    }

    // @Test
    public void test() {
        User user1 = userRepository.findByNameLike("u%");
        Assert.notNull(user1);

        User user2 = userRepository.readByName("user");
        Assert.notNull(user2);

        List<User> users = userRepository.getByCreateDateLessThan(new Date());
    }
}
