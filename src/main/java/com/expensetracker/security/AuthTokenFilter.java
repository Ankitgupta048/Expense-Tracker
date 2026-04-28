package com.expensetracker.security;

import com.expensetracker.entity.AuthToken;
import com.expensetracker.repository.AuthTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenFilter(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/")
                || path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                unauthorized(response, "Missing or invalid auth token");
                return;
            }
            String token = header.substring("Bearer ".length()).trim();
            if (token.isEmpty()) {
                unauthorized(response, "Missing or invalid auth token");
                return;
            }

            AuthToken t = authTokenRepository.findByToken(token).orElse(null);
            if (t == null) {
                unauthorized(response, "Missing or invalid auth token");
                return;
            }
            if (t.getExpiresAt() != null && t.getExpiresAt().isBefore(Instant.now())) {
                unauthorized(response, "Session expired. Please login again.");
                return;
            }

            request.setAttribute(AuthUtil.ATTR_USER_ID, t.getUser().getId());
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            unauthorized(response, "Missing or invalid auth token");
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

