package com.heima.search.interceptor;

import com.heima.model.user.beans.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppTokenInterceptor implements HandlerInterceptor {
    /**
     * 得到header中的用户信息，并把它存入ThreadLocal中
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取userId
        String userId = request.getHeader("userId");
        if (userId != null) {
            //存入当前线程
            ApUser user = new ApUser();
            user.setId(Integer.parseInt(userId));
            AppThreadLocalUtil.setUser(user);
        }
        return true;
    }

    /**
     * 清理线程中的数据
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AppThreadLocalUtil.clear();
    }

}
