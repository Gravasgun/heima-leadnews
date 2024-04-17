package com.heima.app.gateway.filter;

import com.heima.app.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取uri
        String path = request.getURI().getPath();
        //如果是登录请求 直接放行
        if (path.contains("/login")) {
            return chain.filter(exchange);
        }
        //不是登录请求 获取token
        String token = request.getHeaders().getFirst("token");
        log.info("token:{}", token);
        //如果token为空 设置401状态码
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //token不为空 校验token
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            //token过期
            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            //获取用户信息
            Object userId = claimsBody.get("id");
            //存入header(在这段代码中，request.mutate() 方法用于创建原始请求的可变副本。这个副本可以通过一系列的修改操作来改变原始请求的状态，例如添加、删除、更新请求头、查询参数等操作)
            //在本段代码中，mutate() 方法用于创建一个可变的副本，然后通过链式调用 headers() 方法来修改请求头部信息，最后通过 build() 方法构建出修改后的新的 ServerHttpRequest 对象)
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //token有效 直接放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置 值越小 优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
