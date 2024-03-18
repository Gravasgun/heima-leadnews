package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtil {
    private static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 将用户信息存入线程
     *
     * @param user
     */
    public static void setUser(WmUser user) {
        WM_USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理线程中的用户信息
     */
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
