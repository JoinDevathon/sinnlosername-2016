package org.devathon.contest2016.builder;

import org.devathon.contest2016.DevathonPlugin;

import java.lang.reflect.Constructor;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.builder
 */
public interface Builder<T> {

    static <T extends Builder> T of(Class<T> c) {

        final Constructor<T> constructor = (Constructor<T>) DevathonPlugin.helper().reflection().getConstructor(c);
        final T instance = (T) DevathonPlugin.helper().reflection().newInstance(constructor);

        return instance;

    }

    T build();

}
