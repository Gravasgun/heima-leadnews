package com.heima.utils.thread;

import com.heima.model.user.beans.ApUser;

public class AppThreadLocalUtil {
    private static final ThreadLocal<ApUser> APP_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 将用户信息存入线程
     *
     * @param user
     */
    public static void setUser(ApUser user) {
        APP_USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static ApUser getUser() {
        return APP_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理线程中的用户信息
     */
    public static void clear() {
        APP_USER_THREAD_LOCAL.remove();
    }
}
