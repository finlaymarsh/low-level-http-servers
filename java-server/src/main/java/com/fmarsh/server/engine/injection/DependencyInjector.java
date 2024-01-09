package com.fmarsh.server.engine.injection;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

public class DependencyInjector {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyInjector.class);

    public DependencyInjector() {}

    public Map<Class<?>, Object> instantiateSingletonsFromSetOfClasses(final Set<Class<?>> setOfClassesOriginal) {
        Set<Class<?>> classes = new HashSet<>(setOfClassesOriginal);
        Map<Class<?>, Object> beans = new HashMap<>();

        Map<Class<?>, ClassMetadata> metadata = buildClassMetadata(classes);

        instantiateBeansWithZeroArgConstructors(classes, metadata, beans);

        instantiateComplexBeans(classes, metadata, beans);

        if (!classes.isEmpty()) {
            // Todo use better exception here
            throw new RuntimeException("Could not satisfy the dependencies for some of the scanned classes");
        }

        return beans;
    }

    private Map<Class<?>, ClassMetadata> buildClassMetadata(Set<Class<?>> classes) {
        Map<Class<?>, ClassMetadata> metadata = new HashMap<>();

        for (Class<?> clazz : classes) {
            List<Constructor<?>> constructors = List.of(clazz.getConstructors());
            if (constructors.size() > 1) {
                // Todo use better exception here
                throw new RuntimeException("My poor dependency injection engine can only handle 1 constructor per class");
            }

            if (constructors.size() == 1) {
                Constructor<?> constructor = constructors.get(0);
                List<Parameter> parameters = List.of(constructor.getParameters());
                List<Class<?>> orderedParamTypes = new ArrayList<>();
                parameters.forEach(parameter -> orderedParamTypes.add(parameter.getType()));
                metadata.put(clazz, new ClassMetadata(clazz, clazz.getName(), constructor, orderedParamTypes));
            } else {
                // Assume empty constructor
                try {
                    metadata.put(clazz, new ClassMetadata(clazz, clazz.getName(), clazz.getDeclaredConstructor(), Collections.emptyList()));
                } catch (NoSuchMethodException e) {
                    // Todo use better exception here
                    throw new RuntimeException(String.format("Error when scanning bean with name: %s", clazz.getName()));
                }
            }
        }
        return metadata;
    }

    private void instantiateBeansWithZeroArgConstructors(Set<Class<?>> classes, Map<Class<?>, ClassMetadata> metadata, Map<Class<?>, Object> beans) {
        LOGGER.debug("Instantiating beans for all empty constructors");

        Set<Class<?>> instantiatedClasses = new HashSet<>();
        for (ClassMetadata classMetadata : metadata.values()) {
            if (classMetadata.orderedParameterTypes().size() == 0) {
                try {
                    beans.put(classMetadata.clazz(), classMetadata.constructor().newInstance());
                    LOGGER.debug("Instantiated bean with name: {}", classMetadata.name());
                    instantiatedClasses.add(classMetadata.clazz());
                } catch (Exception e) {
                    // Todo use better exception here
                    throw new RuntimeException(String.format("Error when instantiating class: %s", classMetadata.name()));
                }
            }
        }

        // Remove all instantiated classes
        classes.removeAll(instantiatedClasses);
    }

    private void instantiateComplexBeans(Set<Class<?>> classes, Map<Class<?>, ClassMetadata> metadata, Map<Class<?>, Object> beans) {
        LOGGER.debug("Instantiating beans for with complex constructors");

        while (!classes.isEmpty()) {
            Set<Class<?>> instantiatedClasses = new HashSet<>();
            for (Class<?> clazz : classes) {
                ClassMetadata classMetadata = metadata.get(clazz);
                List<Object> arguments = new ArrayList<>();

                boolean allDependenciesSatisfied = true;
                for (Class<?> paramClass : classMetadata.orderedParameterTypes()) {
                    if (beans.containsKey(paramClass)) {
                        arguments.add(beans.get(paramClass));
                    } else {
                        allDependenciesSatisfied = false;
                        break;
                    }
                }

                if (allDependenciesSatisfied) {
                    try {
                        beans.put(classMetadata.clazz(), classMetadata.constructor().newInstance(arguments.toArray()));
                        LOGGER.debug("Instantiated bean with name: {}", classMetadata.name());
                        instantiatedClasses.add(clazz);
                    } catch (Exception e) {
                        // Todo use better exception here
                        throw new RuntimeException(String.format("Error when instantiating class: %s", classMetadata.name()));
                    }
                }
            }

            // No beans instantiated on this pass through, we have beans with dependencies we can never satisfy
            if (instantiatedClasses.isEmpty()) {
                return;
            }

            classes.removeAll(instantiatedClasses);
        }
    }
}
