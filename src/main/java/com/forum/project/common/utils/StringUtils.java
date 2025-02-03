package com.forum.project.common.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {
    public static String toCamelCase(String snakeCase) {
        String[] parts = snakeCase.split("_");
        return parts[0] + Arrays.stream(parts, 1, parts.length)
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining());
    }
}
