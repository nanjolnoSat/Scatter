package com.mishaki.scatter;

import android.os.Looper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Scatter {
    /**
     * 要通知的对象及对应的方法列表
     */
    private final Map<Object, List<MethodInfo>> eventReceiver;
    /**
     * 主线程转换器
     */
    private final MainConverter mainConverter;
    /**
     * 子线程转换器
     */
    private final AsyncConverter asyncConverter;
    /**
     * 子线程队列转换器
     */
    private final AsyncQueueConverter asyncQueueConverter;
    private final Comparator<MethodInfo> priorityComparator;

    private static final class Instance {
        private static final Scatter instance = new Scatter();
    }

    public static Scatter getInstance() {
        return Instance.instance;
    }

    private Scatter() {
        eventReceiver = new HashMap<>();
        mainConverter = new MainConverter();
        asyncConverter = new AsyncConverter();
        asyncQueueConverter = new AsyncQueueConverter();
        priorityComparator = new MethodInfo.Comparator();
    }

    /**
     * 注册当前对象
     *
     * @throws IllegalArgumentException 当receiver里面没有方法有Receive的标签的时候
     */
    public synchronized void register(Object receiver) {
        if (eventReceiver.containsKey(receiver)) {
            return;
        }
        Method[] methods = receiver.getClass().getMethods();
        List<MethodInfo> list = new ArrayList<>();
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & (Modifier.ABSTRACT | Modifier.STATIC)) == 0) {
                Receive receive = method.getAnnotation(Receive.class);
                if (receive != null) {
                    MethodInfo mi = new MethodInfo();
                    mi.methodName = method.getName();
                    mi.tag = receive.tag();
                    mi.priority = receive.priority();
                    mi.threadMode = receive.threadMode();
                    mi.parameterTypes = method.getParameterTypes();
                    list.add(mi);
                }
            }
        }
        if (list.size() != 0) {
            Collections.sort(list, priorityComparator);
            eventReceiver.put(receiver, list);
        } else {
            throw new IllegalArgumentException("Subscriber " + receiver.getClass().getSimpleName()
                    + " and its super classes have no public methods with the @Receive annotation");
        }
    }

    /**
     * 发送到所有方法签名一样的所有方法,去除tag不为空的方法
     */
    public void post(Object... args) {
        List<MethodInfo> receiveMethodList = findMethod(args);
        innerPost(receiveMethodList, args);

    }

    /**
     * 发送到所有方法签名一样的所有方法,包括tag不为空的方法
     */
    public void postContainTag(Object... args) {
        List<MethodInfo> receiveMethodList = findMethodByContainTag(args);
        innerPost(receiveMethodList, args);
    }

    /**
     * 发送到tag相同并且方法签名相同的所有方法
     */
    public void postTag(String tag, Object... args) {
        List<MethodInfo> receiveMethodList = findMethodByTag(tag, args);
        innerPost(receiveMethodList, args);
    }

    /**
     * 取消注册当前对象
     *
     * @throws IllegalArgumentException 当该对象没有注册的时候,抛出该异常
     */
    public synchronized void unregister(Object receiver) {
        if (!eventReceiver.containsKey(receiver)) {
            throw new IllegalArgumentException("Subscriber to unregister was not registered before: " + receiver.getClass());
        }
        mainConverter.release(receiver.hashCode());
        eventReceiver.remove(receiver);
    }

    private List<MethodInfo> findMethodByContainTag(Object[] args) {
        List<MethodInfo> list = new ArrayList<>();
        Set<Map.Entry<Object, List<MethodInfo>>> set = eventReceiver.entrySet();
        for (Map.Entry<Object, List<MethodInfo>> entry : set) {
            for (MethodInfo mi : entry.getValue()) {
                MethodInfo methodInfo = mi.cloneCurrent();
                methodInfo.invokeObj = entry.getKey();
                list.add(methodInfo);
            }
        }
        return findMethodByContainTag(list, args);
    }

    private List<MethodInfo> findMethod(Object[] args) {
        List<MethodInfo> list = new ArrayList<>();
        Set<Map.Entry<Object, List<MethodInfo>>> set = eventReceiver.entrySet();
        for (Map.Entry<Object, List<MethodInfo>> entry : set) {
            for (MethodInfo mi : entry.getValue()) {
                if (textIsEmpty(mi.tag)) {
                    MethodInfo methodInfo = mi.cloneCurrent();
                    methodInfo.invokeObj = entry.getKey();
                    list.add(methodInfo);
                }
            }
        }
        return findMethodByContainTag(list, args);
    }

    private List<MethodInfo> findMethodByTag(String tag, Object[] args) {
        List<MethodInfo> list = new ArrayList<>();
        Set<Map.Entry<Object, List<MethodInfo>>> set = eventReceiver.entrySet();
        for (Map.Entry<Object, List<MethodInfo>> entry : set) {
            for (MethodInfo mi : entry.getValue()) {
                if (!textIsEmpty(mi.tag) && mi.tag.equals(tag)) {
                    MethodInfo methodInfo = mi.cloneCurrent();
                    methodInfo.invokeObj = entry.getKey();
                    list.add(methodInfo);
                }
            }
        }
        return findMethodByContainTag(list, args);
    }

    private List<MethodInfo> findMethodByContainTag(List<MethodInfo> receiveMethodList, Object[] args) {
        ArrayList<String> argClassName = new ArrayList<>();
        for (Object arg : args) {
            argClassName.add(arg.getClass().getSimpleName());
        }
        List<MethodInfo> list = new ArrayList<>();
        for (MethodInfo mi : receiveMethodList) {
            if (mi.parameterTypes != null && mi.parameterTypes.length == argClassName.size()) {
                boolean isSame = true;
                for (int i = 0; i < mi.parameterTypes.length; i++) {
                    if (!mi.parameterTypes[i].getSimpleName().equals(argClassName.get(i))) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    list.add(mi);
                }
            } else if (mi.parameterTypes == null && argClassName.size() == 0) {
                list.add(mi);
            }
        }
        return list;
    }

    private void innerPost(List<MethodInfo> receiveMethodList, Object[] args) {
        for (MethodInfo mi : receiveMethodList) {
            switch (mi.threadMode) {
                case CURRENT:
                    post(mi, args);
                    break;
                case MAIN:
                    if (isMainThread()) {
                        post(mi, args);
                    } else {
                        mainConverter.taskQueue(mi, args);
                    }
                    break;
                case ASYNC:
                    asyncConverter.taskQueue(mi, args);
                    break;
                case ASYNC_QUEUE:
                    asyncQueueConverter.taskQueue(mi, args);
                    break;
                default:
                    throw new IllegalStateException("Unknown thread mode: " + mi.threadMode);
            }
        }
    }

    static void post(MethodInfo methodInfo, Object[] args) {
        try {
            Method method = methodInfo.invokeObj.getClass().getMethod(methodInfo.methodName, methodInfo.parameterTypes);
            method.invoke(methodInfo.invokeObj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean textIsEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    private boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}