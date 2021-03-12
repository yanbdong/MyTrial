package com.cglib;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

import java.lang.reflect.Method;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 12, 2021
 */
public class Basic {

    public static void main(String[] args) {
        new Basic().t();
    }

    public String getA() {
        return "A";
    }

    public String getB() {
        return "B";
    }

    void t() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Basic.class);
        enhancer.setCallbacks(new Callback[]{new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("intercept b");
                Object result = methodProxy.invoke(o, objects);
                System.out.println("intercept a");
                return result;
            }
        }, new InvocationHandler() {

            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                System.out.println("invoke b ");
                Object result = method.invoke(o, objects);
                System.out.println("invoke a");
                return result;
            }
        }});
        enhancer.setCallbackFilter(new CallbackFilter() {

            @Override
            public int accept(Method method) {
                if (method.getName().equalsIgnoreCase("getA")) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        Basic b = (Basic) enhancer.create();
        b.getA();
        b.getB();
    }

}
