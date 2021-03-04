package com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jul 28, 2020
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        imports = {Instant.class, LocalDateTime.class, ZoneId.class})
public interface M {

    M Instance = Mappers.getMapper(M.class);


    @Mapping(source = "name", target = "sb")
    @Mapping(source = "heart", target = "you")
    @Mapping(source = "car", target = "person")
    @Mapping(source = "name", target = "hh")
    B mm(A a);

    default Person sb(Car car) {
        return new Person();
    }

    default String aaaaaa(String car) {
        return "new Person()";
    }

    @Mapping(target = "attributeName", source = "a.name")
    @Mapping(target = "newAttributeValue", source = "b.sb")
    @Mapping(target = "prevAttributeValue", source = "b.you", defaultExpression = "java(Instant.now().toString())")
    @Mapping(target = "updateReason", constant = "your")
//    @Mapping(target = "startTime", expression = "java(LocalDateTime.ofInstant(Instant.ofEpochSecond(b.hh), ZoneId.systemDefault()))")
    @Mapping(target = "startTime", source = "b.hh")
    @Mapping(target = "duration", source = "b.hh", dependsOn = "startTime")
    HasBuilder mm(A a, B b);

    default LocalDateTime intToTime(int time) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault());
    }

    default Duration between(LocalDateTime start) {
        return Duration.between(start, LocalDateTime.now());
    }
}
