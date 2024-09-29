package ru.sakhalin2.caching_proxy;

@FunctionalInterface
public interface ICachingProxyAction {
    public void apply(CachingProxy cachingProxy, CommandSwitch cmd, String args);
}
