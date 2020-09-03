package com.database.repo;

import com.database.module.Department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 31, 2020
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
