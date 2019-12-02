/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 反射相关操作
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class ReflectUtils {

    private ReflectUtils() {
    }

    public static Collection<Class<?>> getClassesByAnnotation(@NonNull final String basePackage,
                                                              @NonNull final Class<? extends Annotation> annotation) {
        return getClasses(basePackage).stream()
                .filter(t -> t.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private static Set<Class<?>> scanFile(URL url, String packageName)
            throws UnsupportedEncodingException, ClassNotFoundException {
        log.info("scanning of file type");
        String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
        return findAndAddClassesInPackageByFile(packageName, filePath);
    }

    private static Set<Class<?>> scanJar(URL url, String packageName, String packageDirName)
            throws IOException, ClassNotFoundException {
        val result = new HashSet<Class<?>>();
        log.info("scanning of jar type");
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (!name.startsWith(packageDirName)) {
                continue;
            }
            int idx = name.lastIndexOf('/');
            if (idx != -1) {
                packageName = name.substring(0, idx).replace('/', '.');
            }
            if (name.endsWith(".class") && !entry.isDirectory()) {
                String className = name.substring(packageName.length() + 1, name.length() - 6);
                result.add(Class.forName(packageName + '.' + className));
            }
        }
        return result;
    }

    /**
     * 从包package中获取所有的Class
     */
    private static Set<Class<?>> getClasses(String packageName) {

        val classes = new HashSet<Class<?>>();
        String packageDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    classes.addAll(scanFile(url, packageName));
                } else if ("jar".equals(protocol)) {
                    classes.addAll(scanJar(url, packageName, packageDirName));
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    private static Set<Class<?>> findAndAddClassesInPackageByFile(String packageName, String packagePath) throws ClassNotFoundException {
        val dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("There is nothing in {} which user defined", packageName);
            return Collections.emptySet();
        }
        val dirFiles = dir.listFiles(file -> file.isDirectory() || (file.getName().endsWith(".class")));
        if (Objects.isNull(dirFiles)) {
            return Collections.emptySet();
        }
        val result = new HashSet<Class<?>>();
        for (val file : dirFiles) {
            if (file.isDirectory()) {
                result.addAll(findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath()));
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                result.add(aClass);
            }
        }
        return result;
    }

}
