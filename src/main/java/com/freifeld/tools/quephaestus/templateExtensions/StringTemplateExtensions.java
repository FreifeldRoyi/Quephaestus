package com.freifeld.tools.quephaestus.templateExtensions;

import io.quarkus.qute.TemplateExtension;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

@TemplateExtension
public class StringTemplateExtensions {
    private static final Pattern PATTERN = Pattern.compile("([0-9a-zA-Z]+)");

    private static Gatherer<String, ?, String> splitter(
            PredicateSlidingWindow shouldSplit,
            Function<Character, Character> splitTransform,
            Function<Character, Character> transformEach
    ) {

        class PartBuilder {
            String builder;

            PartBuilder() {
                this.builder = "";
            }
        }

        BiFunction<char[], Integer, Optional<Character>> findNext =
                (char[] charArray, Integer index) -> switch (index) {
                    case int i when i + 1 == charArray.length -> Optional.empty();
                    case int _ when charArray.length > 0 -> Optional.of(charArray[index + 1]);
                    default -> throw new RuntimeException("Element cannot be null, empty or blank");
                };

        return Gatherer.ofSequential(
                PartBuilder::new,
                (state, element, downstream) -> {
                    if (downstream.isRejecting()) {
                        return false;
                    }

                    final char[] charArray = element.toCharArray();

                    var previous = Optional.of(charArray[0]);
                    var current = charArray[0];
                    var next = findNext.apply(charArray, 0);

                    state.builder += splitTransform.apply(current);

                    for (var i = 1; i < charArray.length; ++i) {
                        current = charArray[i];
                        next = findNext.apply(charArray, i);
                        if (shouldSplit.test(previous, current, next)) {
                            downstream.push(state.builder);
                            state.builder = "" + splitTransform.apply(current);
                        } else {
                            state.builder += transformEach.apply(current);
                        }

                        previous = Optional.of(current);
                    }

                    downstream.push(state.builder);
                    state.builder = "";

                    return true;
                });
    }

    public static String toPascalCase(String value) {
        return processor(
                value,
                (_, current, next) -> Character.isUpperCase(current) && next.map(Character::isLowerCase).orElse(false),
                Character::toUpperCase,
                Function.identity(),
                "");
    }

    private static String processor(String value, PredicateSlidingWindow shouldSplit, Function<Character, Character> splitTransform, Function<Character, Character> transformEach, String joinWith) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Element cannot be null, empty or blank");
        }

        final var matcher = PATTERN.matcher(value);

        return matcher.results()
                .map(result -> result.group(1))
                .gather(splitter(shouldSplit, splitTransform, transformEach))
                .collect(Collectors.joining(joinWith));
    }

    public static String toSnakeCase(String value) {
        return processor(value,
                (prev, cur, next) ->
                        Character.isUpperCase(cur) && prev.filter(p -> !Character.isUpperCase(p)).or(() -> next.filter(Character::isLowerCase)).isPresent(),
                Character::toLowerCase,
                Character::toLowerCase,
                "_");
    }

    public static String toKebabCase(String value) {
        return processor(value,
                (prev, cur, next) ->
                        Character.isUpperCase(cur) && prev.filter(p -> !Character.isUpperCase(p)).or(() -> next.filter(Character::isLowerCase)).isPresent(),
                Character::toLowerCase,
                Character::toLowerCase,
                "-");
    }

    @FunctionalInterface
    private interface PredicateSlidingWindow {
        boolean test(Optional<Character> previous, Character current, Optional<Character> next);
    }

}
