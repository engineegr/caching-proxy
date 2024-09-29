package ru.sakhalin2.caching_proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static ru.sakhalin2.caching_proxy.CachingProxyHelper.*;

/**
 * A command line parser interpreters command line arguments and maps them in
 * the following map-object for {@link CachingProxy}:
 * {@link CommandSwitch} enum object => a string (switch option or option list
 * as a string 'o1,o2,...')
 * 
 * Passing argument examples:
 * <ul>
 * <li>caching-proxy --clear-cache</li>
 * <li>caching-proxy -r</li>
 * <li>caching-proxy --port 35535 --origin https://google.com</li>
 * </ul>
 *
 * @see CachingProxy
 * @see CommandSwitch
 */
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
            Matcher cmdSwitchMatcher = COMBINED_ALL_CMD_SWITCH_REGEXPS_PATTERN.matcher(cmdSwitchArg);
            if (cmdSwitchMatcher.matches() && cmdSwitchMatcher.groupCount() > 0) {
                String cmdSwitchVal = cmdSwitchMatcher.group(1);
                Optional<CommandSwitch> cmdSwitchOptional = Stream.of(CommandSwitch.values())
                        .filter((cmd) -> cmdSwitchVal.length() == 1 ? cmdSwitchVal.equals(cmd.getShortFormat())
                                : cmdSwitchVal.equals(cmd.getLongFormat()))
                        .findFirst();

                var cmdSwitch = cmdSwitchOptional.get();
                if (cmdSwitch.isArgsRequired()) {
                    if (!it.hasNext()) {
                        throw new IllegalArgumentException("Can't match command switch: no options are provided");
                    }
                    var cmdSwitchArguments = it.next();
                    Pattern cmdSwitchValPattern = Pattern.compile(cmdSwitch.getArgumentRegExp());
                    Matcher cmdSwitchValMatcher = cmdSwitchValPattern.matcher(cmdSwitchArguments);
                    if (cmdSwitchValMatcher.matches()) {
                        cmdSwitchMap.put(cmdSwitch, cmdSwitchArguments);
                    } else {
                        throw new IllegalArgumentException("Can't match command switch options: " + cmdSwitchArguments);
                    }
                } else {
                    cmdSwitchMap.put(cmdSwitch, EMPTY_STRING);
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
