package com.liming.hotswap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class HotSwapAgent {


    /**
     * 静态加载。Java agent指定的premain方法，会在main方法之前被调用
     */
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("premain start!");
//        addTransformer(instrumentation);
        System.out.println("premain end!");
    }

    /**
     * 动态加载。请求参数1.jar位置 2.要热加载的类名
     */
    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("agentmain start!");
        System.out.println("请求参数:" + args);
        String[] split = args.split("\\|");
        if (split.length < 2) {
            return;
        }
        String path = split[0];
        Map<String, byte[]> cache = new HashMap<>();
        try (JarFile jarFile = new JarFile(path)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class")) {
                    String className = name.replace(".class", "").replace("/", ".");
                    for (int i = 1; i < split.length; i++) {
                        if (split[i].equals(className)) {
                            byte[] aByte = getByte(jarFile.getInputStream(jarEntry));
                            if (aByte != null) {
                                cache.put(className, aByte);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyClassFileTransformer transformer = new MyClassFileTransformer(cache);
        instrumentation.addTransformer(transformer, true);
        Class<?>[] classes = instrumentation.getAllLoadedClasses();
        for (Class<?> clz : classes) {
            if (cache.containsKey(clz.getName())) {
                try {
                    System.out.println("热交换文件" + clz.getName());
                    instrumentation.retransformClasses(clz);
                } catch (UnmodifiableClassException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 获取失败返回null数组
     *
     */
    public static byte[] getByte(InputStream inputStream) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[0xFFFF];
            for (int len; (len = inputStream.read(buffer)) != -1; ) {
                os.write(buffer, 0, len);
            }
            os.flush();
            return os.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

}