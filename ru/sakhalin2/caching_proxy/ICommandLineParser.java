package ru.sakhalin2.caching_proxy;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ICommandLineParser {

    public static String getCommandSwitchRegExp() {
        Stream<String> t = Stream.of(CommandSwitch.values()).map(cmd -> {
            if (cmd.ordinal() == CommandSwitch.values().length - 1) {
                return cmd.getTitle();
            } else {
                return cmd.getTitle() + "|";
            }
        });
        return t.collect(Collectors.joining());
    }

    public static final Pattern CMD_SWITCH_VALUE_REGEXP_PATTERN = Pattern.compile(
            String.format("(?:--|-)(%s)", getCommandSwitchRegExp()),
            Pattern.UNICODE_CASE);

    public Map<CommandSwitch, String> parse() throws IllegalArgumentException;

}