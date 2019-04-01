package com.mishaki.scatter.util;


import com.mishaki.scatter.Scatter;

public final class ScatterUtil {
    public static void register(Object receiver) {
        Scatter.getInstance().register(receiver);
    }

    public static void unregister(Object receiver) {
        Scatter.getInstance().unregister(receiver);
    }

    public static void post(Object... args) {
        Scatter.getInstance().post(args);
    }

    public static void postContainTag(Object... args) {
        Scatter.getInstance().postContainTag(args);
    }

    public static void postTag(String tag, Object... args) {
        Scatter.getInstance().postTag(tag, args);
    }
}
