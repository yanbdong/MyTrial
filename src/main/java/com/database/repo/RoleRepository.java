package com.database.repo;

import com.database.module.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 31, 2020
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
