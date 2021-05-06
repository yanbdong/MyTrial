package com.database.module;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 31, 2020
 */

@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyBatisUser {

    Long id;
    String name;
    Integer age;
    String email;
}
