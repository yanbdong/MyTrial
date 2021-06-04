package com.alter.dao.rec.mapper;

import org.apache.ibatis.annotations.Update;

import com.alter.dao.rec.entity.Resident;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 数字居民表 Mapper 接口
 * </p>
 *
 * @author yanbdong
 * @since 2021-05-12
 */
public interface ResidentMapper extends BaseMapper<Resident> {

    @Update("ALTER TABLE resident ADD COLUMN identity_category tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '身份类别，1、原著民；2、普通居民'")
    void addIndigenous();
}
