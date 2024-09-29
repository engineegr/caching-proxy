package ru.sakhalin2.caching_proxy;

import java.util.Map;

public class CachingProxy {

    public void setup(Map<CommandSwitch, String> cmdSwitchArgsMap) {
        for (var cmd : cmdSwitchArgsMap.keySet()) { 
            cmd.apply(this, cmdSwitchArgsMap.get(cmd));
        }
    }

    public void setPort(CommandSwitch cmdSwitch, String args) {
        System.out.println(String.format("Set port %d", Integer.parseInt(args)));
    }

    public void setOriginUrl(CommandSwitch cmdSwitch, String args) {
        System.out.println("Set originUrl " + args);
    }

    public void clearCache(CommandSwitch cmdSwitch, String args) {
        System.out.println("Clear cache");
    }

    public static void main(String[] args) {
        var parser = new CommandLineParser(args);
        Map<CommandSwitch, String> cmdSwitchArgsMap = parser.parse();
        var cachingProxy = new CachingProxy();
        cachingProxy.setup(cmdSwitchArgsMap);
    }

}
