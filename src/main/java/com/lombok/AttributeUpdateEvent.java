package com.lombok;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jan 22, 2021
 */
@Getter
@Builder
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AttributeUpdateEvent {

    String attributeName;
    String newAttributeValue;
    String prevAttributeValue;
    String updateReason;
    boolean isUpdateSuccess;
    @Builder.Default
    long timeStamp = Instant.now().toEpochMilli();
}
