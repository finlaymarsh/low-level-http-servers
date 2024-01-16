package com.fmarsh.server.engine.annotation;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DirectoryNode {
    private final String packageName;
    private final String name;
    private boolean isExplored = false;
    private Set<DirectoryNode> subDirectories;
    private Set<Class<?>> classes;

    public DirectoryNode(String packageName) {
        this.packageName = packageName;
        this.name = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public Optional<Set<DirectoryNode>> getSubDirectories() {
        return Optional.ofNullable(subDirectories);
    }

    public Optional<DirectoryNode> getSubDirectory(String subDirectoryName) {
        return subDirectories.stream().filter(subDirectory -> subDirectory.getName().equals(subDirectoryName)).findFirst();
    }

    public Optional<Set<Class<?>>> getClasses() {
        return Optional.ofNullable(classes);
    }

    public void explore() {
        Set<String> files = DirectoryHelper.findAllFilesInAPackage(packageName);

        Set<String> classFiles = DirectoryHelper.setOfJavaClasses(files);
        classes = classFiles.stream()
                .map(classFile -> DirectoryHelper.getClass(classFile, packageName))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toSet());

        subDirectories = DirectoryHelper.setOfSubDirectories(packageName, files);
        isExplored = true;
    }
}
