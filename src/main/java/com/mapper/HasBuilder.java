package com.mapper;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 04, 2021
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class HasBuilder {

    String attributeName;
    String newAttributeValue;
    String prevAttributeValue;
    String updateReason;
    LocalDateTime startTime;
    Duration duration;
}
