package com.jspring.patterns.structural.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class MethodProxy<T> implements InvocationHandler {

    private final T source;
    private final T proxy;

    @SuppressWarnings("unchecked")
    public MethodProxy(T source) {
        this.source = source;
        this.proxy = (T) Proxy.newProxyInstance(source.getClass().getClassLoader(),
            source.getClass().getInterfaces(),
            this);
    }

    public T getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        preInvode(source, method, args);
        Object r = method.invoke(source, args);
        afterInvode(source, method, args, r);
        return r;
    }

    protected abstract void preInvode(T source, Method method, Object[] args);

    protected abstract void afterInvode(T source, Method method, Object[] args,
            Object result);

}
