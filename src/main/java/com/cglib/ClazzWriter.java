package com.cglib;

import java.util.function.Consumer;

import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Factory;

/**
 * @author yanbodong
 * @date 2021/05/23 22:09
 **/
public class ClazzWriter implements Consumer<Class<?>> {

    private DebuggingClassWriter debuggingClassWriter;

    public ClazzWriter() {
        debuggingClassWriter = new DebuggingClassWriter(2);
    }

    @Override
    public void accept(final Class<?> aClass) {
        debuggingClassWriter.visit(52, 1, aClass.getCanonicalName(), null,
            aClass.getSuperclass().getCanonicalName(),
            new String[]{Factory.class.getCanonicalName()});
        debuggingClassWriter.toByteArray();
    }
}
