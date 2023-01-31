package com.liming.hotswap;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

public class MyClassFileTransformer implements ClassFileTransformer {
    private final Map<String, byte[]> cacheByte;

    public MyClassFileTransformer(Map<String, byte[]> cacheByte) {
        this.cacheByte = cacheByte;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return cacheByte.get(className.replace("/", "."));
    }
}
