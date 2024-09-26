package ru.sakhalin2.caching_proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CachingProxy {
    private final Map<CommandSwitch, BiConsumer<CommandSwitch, String>> switchActionMap = new HashMap<>();
    {
        switchActionMap.put(CommandSwitch.PORT_NUMBER, this::setPort);
        switchActionMap.put(CommandSwitch.ORIGIN_URL, this::setOriginUrl);
        switchActionMap.put(CommandSwitch.CLEAR_CACHE, this::clearCache);

    };

    public void apply(CommandSwitch cmdSwitch, String option) {
        BiConsumer<CommandSwitch, String> action = switchActionMap.get(cmdSwitch);
        action.accept(cmdSwitch, option);
    }

    public void setPort(CommandSwitch cmdSwitch, String option) {
        System.out.println(String.format("Set port %d", Integer.parseInt(option)));
    }

    public void setOriginUrl(CommandSwitch cmdSwitch, String originUrl) {
        System.out.println("Set originUrl " + originUrl);
    }

    public void clearCache(CommandSwitch cmdSwitch, String option) {
        System.out.println("Clear cache");
    }

    @SuppressWarnings("unused")
    private void addSwitchAction(CommandSwitch cmdSwitch, BiConsumer<CommandSwitch, String> action) {
        switchActionMap.put(cmdSwitch, action);
    }

    @SuppressWarnings("unused")
    private static List<String> getOptionListHelper(CommandSwitch cmdSwitch, String optionList) {
        if (cmdSwitch.isOptionList()) {
            return List.of(optionList.split(cmdSwitch.getOptionDelimiter()));
        }
        return null;
    }

    public static void main(String[] args) {
        var parser = new CommandLineParser(new String[] { "--origin", "https://uibakery.io/regex-library/url-regex-java" });
        Map<CommandSwitch, String> switchKeyArgMap = parser.parse();
        var cachingProxy = new CachingProxy();
        // Setup our proxy
        for (var it : switchKeyArgMap.entrySet()) {
            cachingProxy.apply(it.getKey(), it.getValue());
        }
    }

}
