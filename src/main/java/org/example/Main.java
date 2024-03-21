package org.example;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.text.ListFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        unnamedVariables();
        foreignFunctionMemoryAPI();
        localeDependentListPatterns();
        streamGatherers();
    }

    static void unnamedVariables() {
        final var value = "2w";
        try {
            final var _ = Integer.parseInt(value);
        } catch (NumberFormatException _) {
            System.err.println("Examples with unnamed variables");
        }

        Stream.of(1, 2, 3, 4)
                .forEach(_ -> System.out.println("foreach with unnamed variable"));
    }

    static void foreignFunctionMemoryAPI() {
        // get strlen of c libraries to get string length
        // which it api you can access native memory

        // get a lookup object for commonly used libraries
        final var stdlib = Linker.nativeLinker().defaultLookup();

        // get a handle to  the strlen funcion in c library
        final var strlen = Linker.nativeLinker()
                .downcallHandle(
                        stdlib.find("strlen").orElseThrow()
                        , FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

        // get confined memory area
        try (final var offHeap = Arena.ofConfined()) {
            // convert Java String to a C string and store in of-heap memory
            final var str = offHeap.allocateFrom("Expedite");
            // invoke the foreign function
            final var len = strlen.invoke(str);
            System.out.println(STR."len = \{len}");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static void localeDependentListPatterns() {
        final var list = List.of("Earth", "21-04-2024", "Burning");
        final var formatter = ListFormat.getInstance(Locale.US, ListFormat.Type.STANDARD, ListFormat.Style.FULL);
        System.out.println(formatter.format(list));
    }

    static void streamGatherers() {
        final var listNew = List.of("test1", "test2", "test3", "test4", "test5", "test6");
        listNew.stream()
                .gather(Gatherers.windowFixed(2))
                .forEach(System.out::println);
    }
}