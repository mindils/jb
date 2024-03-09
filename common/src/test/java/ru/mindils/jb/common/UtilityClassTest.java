package ru.mindils.jb.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UtilityClassTest {

    @Test
    void transformString() {
        String input = "Hello, World!";
        String expected = "HELLO, WORLD!";
        String result = UtilityClass.transformString(input);

        assertEquals(expected, result);
    }
}
