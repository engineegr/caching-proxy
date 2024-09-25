package ru.sakhalin2.caching_proxy;

import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
public interface IProxyApplyFunction extends Consumer<List<String>> {
}
