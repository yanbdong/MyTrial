package com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jul 28, 2020
 */
@Mapper
public interface M {

    M Instance = Mappers.getMapper(M.class);

    @Mapping(source = "name", target = "sb")
    @Mapping(source = "heart", target = "you")
    B mm(A a);

}
