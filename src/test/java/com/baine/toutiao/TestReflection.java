package com.baine.toutiao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.fail;


class methodClass {
    public final int fuck = 3;
    public int add(int a,int b) {
        return a+b;
    }
    public int sub(int a,int b) {
        return a+b;
    }
}

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestReflection{
    @Test
    public void testReflection() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> klass = null;
        try {
            klass = Class.forName("com.baine.toutiao.methodClass");
        } catch (ClassNotFoundException e) {
            fail("Not finding class:" + e.getMessage());
        }
        Object object = klass.newInstance();
        Method method = klass.getMethod("add", int.class, int.class);
        Object result = method.invoke(object, 1, 4);
        System.out.println(result);
    }

}
