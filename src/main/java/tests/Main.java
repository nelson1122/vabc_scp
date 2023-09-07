package main.java.tests;

import java.util.function.Function;

public class Main {

    public static String concat(String s, Integer i, Integer j) {
        return s + i + j;
    }

    public static Function<String, Function<Integer, Function<Integer, String>>> curry() {
        return s -> (i -> (j -> concat(s, i, j)));
    }

    public static void main (String[] args){
        curry().apply("1").apply(2).apply(3);
    }
}
