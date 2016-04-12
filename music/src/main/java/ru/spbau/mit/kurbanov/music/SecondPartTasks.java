package ru.spbau.mit.kurbanov.music;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths
                .stream()
                .flatMap(p -> {
                    try {
                        return Files.lines(Paths.get(p));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .filter(a -> a.contains(sequence))
                .collect(toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {

        class Point {
            private double x;
            private double y;

            private Point(double x, double y) {
                this.x = x;
                this.y = y;
            }
        }

        Random random = new Random();
        final int size = 1000000;
        return Stream.generate(() -> new Point(random.nextDouble(), random.nextDouble()))
                .limit(size)
                .filter(p -> p.x * p.x + p.y * p.y <= 1)
                .count() * 1.0 / size;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        class Pair {
            public String first;
            public int second;

            public Pair(String first, int second) {
                this.first = first;
                this.second = second;
            }

        }

        return compositions
                .entrySet()
                .stream()
                .map(sl ->
                        new Pair(sl.getKey(), sl.getValue()
                                .stream()
                                .mapToInt(String::length)
                                .sum()))
                .max(Comparator.comparing(p -> p.second))
                .map(p -> p.first)
                .orElse(null);
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum));
    }
}
