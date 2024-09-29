package ru.sakhalin2.caching_proxy;

import static ru.sakhalin2.caching_proxy.CachingProxyHelper.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public enum CommandSwitch {

    PORT_NUMBER(
            new CommandOptions("port".concat(CMD_SWITCH_VALUE_DELIMITER).concat("p"), true, false, "", "\\d+"),
            CachingProxy::setPort),
    ORIGIN_URL(new CommandOptions("origin".concat(CMD_SWITCH_VALUE_DELIMITER).concat("u"), true, false, "",
            "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$"),
            CachingProxy::setOriginUrl),
    CLEAR_CACHE(new CommandOptions("clear-cache".concat(CMD_SWITCH_VALUE_DELIMITER).concat("r"), false, false, "",
            ""), CachingProxy::clearCache);

    private final CommandOptions options;

    private final ICachingProxyAction cmdAction;

    private CommandSwitch(CommandOptions anOptions,
            ICachingProxyAction aCmdAction) {
        this.options = anOptions;
        this.cmdAction = aCmdAction;
    }

    public String getTitle() {
        return this.options.title();
    }

    public boolean hasShortFormat() {
        return options.title().contains(CMD_SWITCH_VALUE_DELIMITER);
    }

    public String getLongFormat() {
        return hasShortFormat() ? options.title().split("\\".concat(CMD_SWITCH_VALUE_DELIMITER))[0] : options.title();
    }

    public String getShortFormat() {
        return hasShortFormat() ? options.title().split("\\".concat(CMD_SWITCH_VALUE_DELIMITER))[1] : EMPTY_STRING;
    }

    public String getArgumentRegExp() {
        return options.argRegExp();
    }

    public boolean isArgsRequired() {
        return options.isArgsRequired();
    }

    public boolean isArgList() {
        return options.isArgList();
    }

    public String getArgListDelimiter() {
        return options.argListDelimiter();
    }

    public void apply(CachingProxy cachingProxy, String args) {
        this.cmdAction.apply(cachingProxy, this, args);
    }

    public List<String> extractArgs(String args) throws IllegalArgumentException {
        if (isArgsRequired()) {
            Pattern cmdSwitchValPattern = Pattern.compile(getArgumentRegExp());
            Matcher cmdSwitchValMatcher = cmdSwitchValPattern.matcher(args);
            if (cmdSwitchValMatcher.matches()) {
                return List.of(args.split(getArgumentRegExp()));
            } else {
                throw new IllegalArgumentException("Can't match command switch options: " + args);
            }
        }
        return null;
    }
}
