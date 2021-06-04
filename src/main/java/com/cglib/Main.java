package com.cglib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.cglib.IMapper.SpiritMapper;
import com.cglib.IWorker.Worker;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yanbodong
 * @date 2021/05/23 21:28
 **/
public class Main {

    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        // 创建worker目标对象
        IWorker worker = new Worker();
        // 创建mapper目标对象
        IMapper mapper = new SpiritMapper();
        // 生成worker代理对象
        IWorker workerProxy = (IWorker) Proxy
            .newProxyInstance(classLoader, new Class<?>[]{IWorker.class},
                new Aspect().setTargetObject(worker));
        // 生成mapper代理对象
        IMapper mapperProxy = (IMapper) Proxy
            .newProxyInstance(classLoader, new Class<?>[]{IMapper.class},
                new Aspect().setTargetObject(worker));
    }

    @Data
    @Accessors(chain = true)
    public static class Aspect implements InvocationHandler {

        private Object targetObject;

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws Throwable {
            Object result = method.invoke(targetObject, args);
            doProxyWorkAfterDoWork();
            return result;
        }

        private void doProxyWorkAfterDoWork() {

        }
    }
}
