package ru.sakhalin2.caching_proxy;

public record CommandOptions(String title, boolean isArgsRequired, boolean isArgList, String argListDelimiter,
        String argRegExp) {
}
