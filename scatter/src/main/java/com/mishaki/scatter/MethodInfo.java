package com.mishaki.scatter;

class MethodInfo {
    /**
     * invoke需要的对象
     */
    Object invokeObj;
    /**
     * 方法名称
     */
    String methodName;
    /**
     * 优先级
     */
    int priority;
    /**
     * 参数类型
     */
    Class[] parameterTypes;
    ThreadMode threadMode;
    String tag;

    /**
     * clone当前对象
     */
    MethodInfo cloneCurrent() {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.invokeObj = invokeObj;
        methodInfo.methodName = methodName;
        methodInfo.priority = priority;
        methodInfo.parameterTypes = parameterTypes;
        methodInfo.threadMode = threadMode;
        methodInfo.tag = tag;
        return methodInfo;
    }

    static class Comparator implements java.util.Comparator<MethodInfo> {
        @Override
        public int compare(MethodInfo o1, MethodInfo o2) {
            return o2.priority - o1.priority;
        }
    }
}
