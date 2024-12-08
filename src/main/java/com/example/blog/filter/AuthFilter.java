package com.example.blog.filter;

import com.example.blog.context.CurrentUserContext;
import com.example.blog.model.User;
import com.example.blog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CurrentUserContext currentUserContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logger.info("Processing request: " + request.getRequestURI());
            String token = request.getHeader("Authorization");
            if(token == null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"No token provided\"}");
                return;
            }
            if(token.startsWith("Bearer ")){
                token = token.substring(7);
            }
            if(!jwtUtils.validateJwtToken(token)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return;
            }

            User user = jwtUtils.getUserFromToken(token);
            currentUserContext.setCurrentUser(user);


            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing request", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/public") ||
                request.getRequestURI().startsWith("/api/user/login") ||
                request.getRequestURI().startsWith("/api/user/create");
    }
}