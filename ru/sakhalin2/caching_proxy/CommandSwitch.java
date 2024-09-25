package ru.sakhalin2.caching_proxy;

public enum CommandSwitch {
    PORT_NUMBER("port|p", true, false, "", "\\d+"),
    ORIGIN_URL("origin|u", true, false, "",
            "https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)"),
    CLEAR_CACHE("clear-cache|r", false, false, "", "");

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
