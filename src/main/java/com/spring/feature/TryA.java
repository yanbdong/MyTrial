package com.spring.feature;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author yanbodong
 * @date 2021/05/06 10:49
 **/
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TryA {

    public TryA(TryA another) {
        this.another = another;
    }

    TryA another;
    String s;
}
