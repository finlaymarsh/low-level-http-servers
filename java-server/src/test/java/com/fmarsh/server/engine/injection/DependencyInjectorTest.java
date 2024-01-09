package com.fmarsh.server.engine.injection;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DependencyInjectorTest {

    @Test
    void injectionTest() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ClassA.class);
        classes.add(ClassB.class);
        classes.add(ClassC.class);
        classes.add(ClassD.class);
        classes.add(ClassE.class);

        DependencyInjector dependencyInjector = new DependencyInjector();

        assertDoesNotThrow(() -> dependencyInjector.instantiateSingletonsFromSetOfClasses(classes));
    }

    public interface TestClass {
        String sayHello();
    }

    public static class ClassA implements TestClass {

        @Override
        public String sayHello() {
            return "Class A says hello!";
        }
    }

    public static class ClassB implements TestClass {
        private static final Logger LOGGER = LoggerFactory.getLogger(ClassB.class);

        @Override
        public String sayHello() {
            return "Class B says hello!";
        }
    }

    public static class ClassC implements TestClass {

        public ClassC(ClassA classA, ClassB classB){
            classA.sayHello();
            classB.sayHello();
        }

        @Override
        public String sayHello() {
            return "Class C says hello!";
        }
    }

    public static class ClassD implements TestClass {

        public ClassD(ClassC classC){
            classC.sayHello();
        }

        @Override
        public String sayHello() {
            return "Class D says hello!";
        }
    }

    public static class ClassE implements TestClass {

        public ClassE(ClassA classA, ClassB classB, ClassD classD){
            classA.sayHello();
            classB.sayHello();
            classD.sayHello();
        }

        @Override
        public String sayHello() {
            return "Class E says hello!";
        }
    }
}
