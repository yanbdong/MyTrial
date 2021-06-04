package com.cglib;

import java.lang.reflect.Method;

import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.annotation.Configuration;

import com.cglib.IWorker.Worker;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 12, 2021
 */
public class MyCallSite {

    public static void main(String[] args) {
//        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,
//            "/Users/yanbodong/Documents/workspace/MyTrial/");
        t().doWorker();
//        ClazzWriter clazzWriter = new ClazzWriter();
//        clazzWriter.accept(t().getClass());
    }

    public String getA() {
        return "A";
    }

    public String getB() {
        return "B";
    }

    static Worker t() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Worker.class);
        enhancer.setCallbacks(new Callback[]{new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects,
                MethodProxy methodProxy) throws Throwable {
                System.out.println("intercept b");
                Object result = methodProxy.invoke(o, objects);
                System.out.println("intercept a");
                return result;
            }
//        }, new InvocationHandler() {
//
//            @Override
//            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
//                System.out.println("invoke b ");
//                Object result = method.invokeSuper(o, objects);
//                System.out.println("invoke a");
//                return result;
        }
        });
//        enhancer.setCallbackFilter(new CallbackFilter() {
//
//            @Override
//            public int accept(Method method) {
//                if (method.getName().equalsIgnoreCase("getA")) {
//                    return 0;
//                } else {
//                    return 1;
//                }
//            }
//        });
        return (Worker) enhancer.create();
    }
//
//    private static void saveProxyFile(Class<?> clazz) {
//        FileOutputStream out = null;
//        try {
//            byte[] classFile = ProxyGenerator.generateProxyClass(clazz.getName(), clazz);
//            out = new FileOutputStream("/Users/yanbodong/Documents/workspace/MyTrial/com/sun/proxy");
//            out.write(classFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.flush();
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    void beanGenerate() {
        BeanGenerator beanGenerator = new BeanGenerator();
//        beanGenerator.addProperty();
    }

    void fastClass() {
        FastClass fastClass = FastClass.create(MyCallSite.class);
        FastMethod method = fastClass.getMethod("getA", new Class<?>[0]);
//        method.invoke()
    }

}
