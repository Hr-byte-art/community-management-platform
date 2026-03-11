package com.community.interceptor;

import com.community.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if ((token == null || token.isBlank()) && request.getParameter("token") != null) {
            token = request.getParameter("token");
        }
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && !token.isBlank()) {
            if (jwtUtil.validateToken(token)) {
                request.setAttribute("userId", jwtUtil.getUserId(token));
                request.setAttribute("username", jwtUtil.getUsername(token));
                request.setAttribute("role", jwtUtil.getRole(token));
                return true;
            }
        }
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或token已过期\"}");
        return false;
    }
}
