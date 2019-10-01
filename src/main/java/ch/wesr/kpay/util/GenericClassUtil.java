package ch.wesr.kpay.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericClassUtil {

    static public Class[] getGenericType(Class clazz) {
        Type mySuperclass = clazz.getGenericSuperclass();

        if (!(mySuperclass instanceof  ParameterizedType)) throw new RuntimeException("Class instance MUST by created via anonymous class definition in order for generic info to be supported in Java. i.e. new MyClass() {};");

        ParameterizedType pType = (ParameterizedType) mySuperclass;

        Type[] actualTypeArguments = pType.getActualTypeArguments();

        Class[] result  = new Class[actualTypeArguments.length];

        for (int i = 0; i < actualTypeArguments.length; i++) {

            try {
                result[i] = Class.forName(actualTypeArguments[i].getTypeName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
