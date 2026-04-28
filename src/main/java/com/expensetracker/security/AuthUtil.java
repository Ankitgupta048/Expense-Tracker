package com.expensetracker.security;

import com.expensetracker.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;

public final class AuthUtil {
    private AuthUtil() {}

    public static final String ATTR_USER_ID = "auth.userId";

    public static Long requireUserId(HttpServletRequest request) {
        Object v = request.getAttribute(ATTR_USER_ID);
        if (v instanceof Long id) return id;
        throw new UnauthorizedException("Missing or invalid auth token");
    }
}

