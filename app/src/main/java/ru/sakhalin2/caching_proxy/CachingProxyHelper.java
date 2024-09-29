package ru.sakhalin2.caching_proxy;

import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public final class CachingProxyHelper {

    public static String getCombinedCommandSwitchRegExp() {
        Stream<String> t = Stream.of(CommandSwitch.values()).map(cmd -> {
            if (cmd.ordinal() == CommandSwitch.values().length - 1) {
                return cmd.getTitle();
            } else {
                return cmd.getTitle().concat(CMD_SWITCH_VALUE_DELIMITER);
            }
        });
        return t.collect(Collectors.joining());
    }

    public static final String EMPTY_STRING = "";

    public static final String CMD_SWITCH_VALUE_DELIMITER = "|";

    public static final Pattern COMBINED_ALL_CMD_SWITCH_REGEXPS_PATTERN = Pattern.compile(
            String.format("(?:--|-)(%s)", getCombinedCommandSwitchRegExp()),
            Pattern.UNICODE_CASE);

    private CachingProxyHelper() {
     //   
    }    
}
