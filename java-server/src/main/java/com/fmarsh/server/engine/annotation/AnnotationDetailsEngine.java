package com.fmarsh.server.engine.annotation;

import com.fmarsh.server.annotation.details.MappingAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationDetailsEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDetailsEngine.class);
    private static final String ANNOTATION_DETAILS_PACKAGE = "com.fmarsh.server.annotation";

    public static void main(String[] args) {
        DirectoryNode directoryNode = new DirectoryNode(ANNOTATION_DETAILS_PACKAGE);
        directoryNode.explore();
        directoryNode.getSubDirectories().ifPresent(x -> x.forEach(node -> LOGGER.info(node.getName())));

        directoryNode.getSubDirectory("details").ifPresent(details -> {
            details.explore();
            details.getClasses().ifPresent(x -> x.forEach(clazz -> LOGGER.info(clazz.getSimpleName())));
        });

        directoryNode.getSubDirectory("mapping").ifPresent(details -> {
            details.explore();
            details.getClasses().ifPresent(x -> x.forEach(clazz -> LOGGER.info("{} : {}", clazz.getSimpleName(), clazz.isAnnotationPresent(MappingAnnotation.class))));
        });
    }
}
