package com.freifeld.tools.quephaestus.templateExtensions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringTemplateExtensionsTest {
    private static Stream<Arguments> provideCases() {
        return Stream.of(
                argumentsFor("asdASD, fffeeqw123CCCCaaa", "AsdASDFffeeqw123CCCCaaa", "asd-asd-fffeeqw123-ccc-caaa", "asd_asd_fffeeqw123_ccc_caaa"),
                argumentsFor("payment2policies", "Payment2policies", "payment2policies", "payment2policies")
        );
    }

    private static Arguments argumentsFor(
            String original,
            String pascalCase,
            String kebabCase,
            String snakeCase
    ) {
        return Arguments.of(original, pascalCase, kebabCase, snakeCase);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    public void testEmptyValues(String original) {
        assertThrows(RuntimeException.class, () -> StringTemplateExtensions.toPascalCase(original), "Element cannot be null, empty or blank");
        assertThrows(RuntimeException.class, () -> StringTemplateExtensions.toKebabCase(original), "Element cannot be null, empty or blank");
        assertThrows(RuntimeException.class, () -> StringTemplateExtensions.toSnakeCase(original), "Element cannot be null, empty or blank");
    }

    @ParameterizedTest
    @MethodSource("provideCases")
    public void testCaseSwitching(String original,
                                  String pascalCase,
                                  String kebabCase,
                                  String snakeCase) {
        assertEquals(pascalCase, StringTemplateExtensions.toPascalCase(original));
        assertEquals(kebabCase, StringTemplateExtensions.toKebabCase(original));
        assertEquals(snakeCase, StringTemplateExtensions.toSnakeCase(original));
    }
}