package com.fmarsh.server.engine.injection;

import com.fmarsh.server.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

public class ExampleDependencyInjector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleDependencyInjector.class);
    private final static Set<Class<?>> classes = new HashSet<>();
    private final static Map<String, Object> beans = new HashMap<>();
    private final static Map<String, ClassMetaData> metaData = new HashMap<>();

    public static void main(String[] args) {
        classes.add(ClassA.class);
        classes.add(ClassB.class);
        classes.add(ClassC.class);
        classes.add(ClassD.class);
        classes.add(ClassE.class);

        // Scan classes add add to metaData map
        classes.forEach(clazz-> {
//            logClass(clazz);
            List<Constructor<?>> constructors = List.of(clazz.getConstructors());
            if (constructors.size() > 1) {
                LOGGER.info("My poor dependency injection engine can only handle 1 constructor per class");
                return;
            }

            if (constructors.size() == 1) {
                Constructor<?> constructor = constructors.get(0);
                List<Parameter> parameters = List.of(constructor.getParameters());
                List<Class<?>> orderedParamTypes = new ArrayList<>();
                parameters.forEach(parameter -> orderedParamTypes.add(parameter.getType()));
                metaData.put(clazz.getName(), new ClassMetaData(clazz, clazz.getName(), constructor, orderedParamTypes));
                return;
            }
            try {
                metaData.put(clazz.getName(), new ClassMetaData(clazz, clazz.getName(), clazz.getDeclaredConstructor(), Collections.emptyList()));
            } catch (NoSuchMethodException e) {
                LOGGER.info("Error when scanning bean with name: {}", clazz.getName());
            }
        });

        LOGGER.info("MetaData Map");
        metaData.values().forEach(metaData -> LOGGER.info("{} {}", metaData.name(), metaData.orderedParameterTypes().toString()));

        LOGGER.info("Instantiating beans for all empty constructors");
        metaData.values().forEach(metaData -> {
            if (metaData.orderedParameterTypes().size() == 0) {
                try {
                    beans.put(metaData.name(), metaData.constructor().newInstance());
                    LOGGER.info("Instantiated bean with name: {}", metaData.name());
                    classes.remove(metaData.clazz());
                } catch (Exception e) {
                    LOGGER.info("Error when instantiating bean with name: {}", metaData.name());
                }
            }
        });

        // Todo: Update the algorithm below so that it keeps iterating over metadata until all beans instantiated
        // Todo: Skip bean initialisation if not all of the params are created as beans yet.
        // Todo: Scan all classes with @Component annotation

        LOGGER.info("Instantiating beans for with complex constructors");
        for (Class<?> clazz : classes) {
            ClassMetaData clazzMetaData = metaData.get(clazz.getName());
            List<Class<?>> parameters = clazzMetaData.orderedParameterTypes();
            List<Object> arguments = new ArrayList<>();
            for (Class<?> paramClass : parameters) {
                LOGGER.info("{} {}", paramClass.getSimpleName(), beans.containsKey(paramClass.getName()));
                arguments.add(beans.get(paramClass.getName()));
            }
            try {
                beans.put(clazzMetaData.name(), clazzMetaData.constructor().newInstance(arguments.toArray()));
                LOGGER.info("Instantiated bean with name: {}", clazzMetaData.name());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("Error when instantiating bean with name: {}", clazzMetaData.name());
            }
        }
    }

    private static void logClass(Class<?> clazz) {
        LOGGER.info("++++++++++");
        LOGGER.info(clazz.getName());
        List<Constructor<?>> constructors = List.of(clazz.getConstructors());
        constructors.forEach(constructor -> {
            List<Parameter> parameters = List.of(constructor.getParameters());
            parameters.forEach(parameter -> {
                LOGGER.info("Parameter: {} {}", parameter.getType().getName(), parameter.getName());
            });
        });
        LOGGER.info("----------");
    }
    @Component
    private static class ClassA{
        private static final Logger LOGGER = LoggerFactory.getLogger(ClassA.class);

        public void sayHello() {
            LOGGER.info("Class A says hello!");
        }
    }

    @Component
    private static class ClassB{
        private static final Logger LOGGER = LoggerFactory.getLogger(ClassB.class);

        public void sayHello() {
            LOGGER.info("Class B says hello!");
        }
    }

    @Component
    private static class ClassC {

        public ClassC(ClassA classA, ClassB classB){
            classA.sayHello();
            classB.sayHello();
        }

        public void sayHello() {
            LOGGER.info("Class C says hello!");
        }
    }

    @Component
    private static class ClassD {

        public ClassD(ClassC classC){
            classC.sayHello();
        }

        public void sayHello() {
            LOGGER.info("Class D says hello!");
        }
    }

    @Component
    private static class ClassE {

        public ClassE(ClassA classA, ClassB classB, ClassD classD){
            classA.sayHello();
            classB.sayHello();
            classD.sayHello();
        }

        public void sayHello() {
            LOGGER.info("Class E says hello!");
        }
    }
}
