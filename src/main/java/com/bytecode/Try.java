package com.bytecode;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jan 18, 2021
 */
public class Try {

    private static final int INT = 1;
    private static int sInt = 1;
    private final int mINT = 1;
    public int mInt = 1;

    public static void main(String[] args) {
        final int a = 1;
        int b;
        try {
            System.out.println("1");
        } catch (Exception e) {
            try {
                System.out.println("2");
            } catch (Exception ex) {
                System.out.println("3");
            } finally {
                System.out.println("4");
            }
            throw new RuntimeException();
        } finally {
            try {
                System.out.println("5");
            } catch (Exception ex) {
                System.out.println("6");
            } finally {
                System.out.println("7");
            }
        }
    }

    void mVoid() throws Throwable {
        setInt();
        setsInt();
    }

    final void setInt() throws Throwable {
        MethodType methodType = MethodType.methodType(this.getClass());
        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(this.getClass(), "setsInt", methodType).bindTo(this);

        ConstantCallSite constantCallSite = new ConstantCallSite(methodHandle);
        constantCallSite.dynamicInvoker().invoke();

        MutableCallSite mutableCallSite = new MutableCallSite(methodType);
        mutableCallSite.setTarget(methodHandle);
        mutableCallSite.dynamicInvoker().invoke();
    }

    private void setsInt() {
        new Thread(this::d);
    }

    public void d() {
        List<Integer> list = new ArrayList<>();
        list.forEach(this::setA);
    }

    public void setA(int i) {
        i++;
    }
}
