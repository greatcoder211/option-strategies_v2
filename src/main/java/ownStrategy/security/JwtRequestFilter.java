package ownStrategy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ownStrategy.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Wyciągamy nagłówek "Authorization"
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Sprawdzamy, czy nagłówek istnieje i zaczyna się od "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Wycinamy "Bearer "
            username = jwtUtils.extractUsername(jwt);
        }

        // 3. Jeśli mamy login i nikt nie jest jeszcze zalogowany w tym żądaniu
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Jeśli bilet jest ważny, meldujemy to systemowi
            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Wkładamy "kartę dostępu" do schowka Springa
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Puszczamy zapytanie dalej przez kolejkę filtrów
        filterChain.doFilter(request, response);
    }
}