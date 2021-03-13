package com.cglib;

import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.proxy.*;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.Method;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 12, 2021
 */
public class MyCallSite {

    public static void main(String[] args) {
        new MyCallSite().t();
    }

    public String getA() {
        return "A";
    }

    public String getB() {
        return "B";
    }

    void t() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MyCallSite.class);
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
        MyCallSite b = (MyCallSite) enhancer.create();
        b.getA();
        b.getB();
    }

    void beanGenerate(){
        BeanGenerator beanGenerator = new BeanGenerator();
//        beanGenerator.addProperty();
    }

    void fastClass(){
        FastClass fastClass = FastClass.create(MyCallSite.class);
        FastMethod method = fastClass.getMethod("getA", new Class<?>[0]);
//        method.invoke()
    }

}
