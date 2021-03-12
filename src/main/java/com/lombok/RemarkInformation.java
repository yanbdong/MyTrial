package com.lombok;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 10, 2021
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
class RemarkInformation {

    /**
     * Category for this information
     */
    String key;
    String subKey;

    @Builder.Default
    ExecMsg.TextMsgLevel level = ExecMsg.TextMsgLevel.Normal;

    @Builder.Default
    LocalDateTime timeStamp = LocalDateTime.now();

    String content;
}
