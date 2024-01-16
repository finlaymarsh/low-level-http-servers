package com.fmarsh.server.engine.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectoryHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryHelper.class);
    private static final String JAR_LOCATION = AnnotationDetailsEngine.class.getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath();

    public static Set<String> findAllFilesInAPackage(String packageName) {
        String packageUrl = packageName.replaceAll("\\.", "/");
        LOGGER.info(packageUrl);
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageUrl)) {
            assert inputStream != null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return bufferedReader.lines().collect(Collectors.toSet());
        } catch (IOException e) {
            // Todo better exception handling
            throw new RuntimeException(e);
        }
    }

    public static Set<String> setOfJavaClasses(Set<String> files) {
        Set<String> classes = new HashSet<>(files);
        return classes.stream().filter(name -> name.endsWith(".class")).collect(Collectors.toSet());
    }

    public static Set<DirectoryNode> setOfSubDirectories(String packageName, Set<String> files) {
        String packageUrl = packageName.replaceAll("\\.", "/");
        Set<String> subDirectories = new HashSet<>(files);
        return subDirectories.stream().filter(fileName -> {
            File file = new File(String.format("%s%s/%s/", JAR_LOCATION, packageUrl, fileName));
            return file.isDirectory();
        }).map(filename -> new DirectoryNode(packageName + "." + filename.replaceAll("\\.", "/"))).collect(Collectors.toSet());
    }

    public static Optional<Class<?>> getClass(String className, String packageName) {
        try {
            String classSimpleName = className.substring(0, className.lastIndexOf('.'));
            return Optional.of(Class.forName(String.format("%s.%s", packageName, classSimpleName)));
        } catch (ClassNotFoundException e) {
            // Todo consider exception handling
            return Optional.empty();
        }
    }
}
