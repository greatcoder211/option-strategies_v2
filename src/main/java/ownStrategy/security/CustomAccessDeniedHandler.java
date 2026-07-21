package ownStrategy.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Tutaj postaw breakpoint (kliknij na marginesie w IDE, aby pojawiła się czerwona kropka)
        String requestUri = request.getRequestURI();
        String errorMessage = accessDeniedException.getMessage();

        // Wypisanie szczegółów do konsoli
        System.out.println("DEBUG: 403 Forbidden intercepted!");
        System.out.println("DEBUG: Blocked URI: " + requestUri);
        System.out.println("DEBUG: Reason: " + errorMessage);

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }
}