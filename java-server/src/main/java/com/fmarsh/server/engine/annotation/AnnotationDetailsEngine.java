package com.fmarsh.server.engine.annotation;


import java.util.*;

public class AnnotationDetailsEngine {
    private static final String ANNOTATION_DETAILS_PACKAGE = "com.fmarsh.server.annotation";

    public static Map<Class<?>, Set<Class<?>>> constructAnnotationLookupIndex() {
        Map<Class<?>, Set<Class<?>>> annotationLookupIndex = new HashMap<>();

        DirectoryNode annotationNode = new DirectoryNode(ANNOTATION_DETAILS_PACKAGE + ".details");
        annotationNode.explore();
        annotationNode.getClasses().ifPresent(classSet -> classSet.forEach(clazz -> annotationLookupIndex.put(clazz, new HashSet<>())));

        DirectoryNode rootNode = new DirectoryNode(ANNOTATION_DETAILS_PACKAGE);

        Queue<DirectoryNode> queue = new LinkedList<>();
        queue.add(rootNode);

        while (!queue.isEmpty()) {
            DirectoryNode node = queue.remove();
            if (node.getName().equals("details"))
                continue;
            node.explore();
            node.getClasses().ifPresent(classSet -> classSet.forEach(clazz -> {
                Arrays.stream(clazz.getAnnotations()).forEach(annnotation -> {
                    if (annotationLookupIndex.containsKey(annnotation.annotationType())) {
                        annotationLookupIndex.get(annnotation.annotationType()).add(clazz);
                    }
                });
            }));
            node.getSubDirectories().ifPresent(queue::addAll);
        }

        return annotationLookupIndex;
    }
}
