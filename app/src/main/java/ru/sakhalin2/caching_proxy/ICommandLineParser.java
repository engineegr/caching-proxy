package ru.sakhalin2.caching_proxy;

import java.util.Map;

public interface ICommandLineParser {

    public Map<CommandSwitch, String> parse() throws IllegalArgumentException;

}