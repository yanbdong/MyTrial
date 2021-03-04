package com.mapper.builder;

import org.mapstruct.Mapper;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 04, 2021
 */
@Mapper
interface PersonMapper {

    Person map(PersonDto dto);
}

