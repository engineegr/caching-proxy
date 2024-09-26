package ru.sakhalin2.caching_proxy;

import static ru.sakhalin2.caching_proxy.CachingProxyUtils.*;

public enum CommandSwitch {

    PORT_NUMBER("port".concat(CMD_SWITCH_VALUE_DELIMITER).concat("p"), true, false, "", "\\d+"),
    ORIGIN_URL("origin".concat(CMD_SWITCH_VALUE_DELIMITER).concat("u"), true, false, "",
            "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$"),
    CLEAR_CACHE("clear-cache".concat(CMD_SWITCH_VALUE_DELIMITER).concat("r"), false, false, "", "");

    private final String title;

    private final boolean optionSupplier;

    private final boolean optionList;

    private final String optionDelimiter;

    private final String optionRegExp;

    private CommandSwitch(String aTitle, boolean aValueSupplier, boolean aListValue, String aDelimiter,
            String aValueRegExp) {
        this.title = aTitle;
        this.optionSupplier = aValueSupplier;
        this.optionList = aListValue;
        this.optionDelimiter = aDelimiter;
        this.optionRegExp = aValueRegExp;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasShortFormat() {
        return title.contains(CMD_SWITCH_VALUE_DELIMITER);
    }

    public String getLongFormat() {
        return hasShortFormat() ? title.split("\\".concat(CMD_SWITCH_VALUE_DELIMITER))[0] : title;
    }

    public String getShortFormat() {
        return hasShortFormat() ? title.split("\\".concat(CMD_SWITCH_VALUE_DELIMITER))[1] : EMPTY_STRING;
    }

    public String getOptionRegExp() {
        return optionRegExp;
    }

    public boolean isOptionSupplier() {
        return optionSupplier;
    }

    public boolean isOptionList() {
        return optionList;
    }

    public String getOptionDelimiter() {
        return optionDelimiter;
    }
}
