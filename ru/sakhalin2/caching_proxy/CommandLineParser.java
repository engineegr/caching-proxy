package ru.sakhalin2.caching_proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineParser implements ICommandLineParser {

    private final List<String> argList;

    public CommandLineParser(String[] args) {
        this.argList = List.of(args);
    }

    @Override
    public Map<CommandSwitch, String> parse() throws IllegalArgumentException {
        Map<CommandSwitch, String> cmdSwitchMap = new HashMap<>();
        Iterator<String> it = argList.iterator();

        while (it.hasNext()) {
            var cmdSwitchArg = it.next();
            Matcher cmdSwitchMatcher = ICommandLineParser.CMD_SWITCH_VALUE_REGEXP_PATTERN.matcher(cmdSwitchArg);
            if (cmdSwitchMatcher.matches() && cmdSwitchMatcher.groupCount() > 0) {
                String cmdSwitchVal = cmdSwitchMatcher.group(1);
                CommandSwitch cmdSwitch = CommandSwitch.valueOf(CommandSwitch.class, cmdSwitchVal);
                if (cmdSwitch.isOptionSupplier()) {
                    var cmdSwitchArgValue = it.next();
                    Pattern cmdSwitchValPattern = Pattern.compile(cmdSwitch.getOptionRegExp());
                    Matcher cmdSwitchValMatcher = cmdSwitchValPattern.matcher(cmdSwitchArgValue);
                    if (cmdSwitchValMatcher.matches()) {
                        cmdSwitchMap.put(cmdSwitch, cmdSwitchArgValue);
                    }
                    {
                        throw new IllegalArgumentException("Can't match command switch value " + cmdSwitchArgValue);
                    }

                }
            } else {
                throw new IllegalArgumentException("Can't match command switch " + cmdSwitchArg);
            }

        }
        return cmdSwitchMap;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null) {
            return false;
        }
        if (this == otherObject) {
            return true;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }

        CommandLineParser commandLineParser = (CommandLineParser) otherObject;
        return argList.equals(commandLineParser.argList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argList);
    }

}
