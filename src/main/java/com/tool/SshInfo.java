package com.tool;

import lombok.Builder;
import lombok.Getter;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 03, 2020
 */
@Builder
@Getter
class SshInfo {

    private String ip;
    @Builder.Default
    private int port = 22;
    private String name;
    private String password;

}
