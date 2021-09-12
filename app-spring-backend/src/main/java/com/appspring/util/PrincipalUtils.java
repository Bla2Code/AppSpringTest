package com.appspring.util;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

public class PrincipalUtils {

    private final static String AUTH_ERROR_MESSAGE = "Ошибка аутентификации пользователя";

    public static String getUsernameByPrincipal(Object principal) {
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERROR_MESSAGE);
        }
        return username;
    }

    public static String getLoggedUsername() {
        return PrincipalUtils.getUsernameByPrincipal(getLoggedPrincipal());
    }

    public static Object getLoggedPrincipal() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
