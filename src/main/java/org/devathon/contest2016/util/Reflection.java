package org.devathon.contest2016.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.util
 */
public class Reflection {

    public Constructor<?> getConstructor(Class<?> c, Class<?>... classes) {
        try {
            return c.getConstructor(classes);
        } catch (NoSuchMethodException e) {
            return throwSneaky(e);
        }
    }

    public Object newInstance(Constructor<?> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return throwSneaky(e);
        }
    }

    private <T> T throwSneaky(Throwable t) {
        throw new ReflectionException(t);
    }

    private class ReflectionException extends RuntimeException {
        public ReflectionException(Throwable cause) {
            super(cause);
        }
    }


}
