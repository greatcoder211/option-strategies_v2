package ownStrategy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    // // Pobieramy wartości z mostu konfiguracyjnego (application.properties / Env Vars)
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    // // CZĘŚĆ 1: Przygotowanie matematycznej matrycy (SecretKey)
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // // CZĘŚĆ 2: Drukarka - tworzenie nowego biletu (tokena)
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername()) // // Wpisujemy "kto to jest" (Subject)
                .issuedAt(new Date(System.currentTimeMillis())) // // Kiedy wystawiono
                .expiration(new Date(System.currentTimeMillis() + expiration)) // // Kiedy wygasa
                .signWith(getSigningKey()) // // Przybicie pieczątki naszym kluczem
                .compact(); // // Laminowanie w jeden ciąg znaków
    }
    // // CZĘŚĆ 3: Czytnik - wyciąganie nazwy użytkownika z tokena
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // // CZĘŚĆ 3: Weryfikator - sprawdzenie, czy token jest autentyczny i ważny
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // // Sprawdzamy czy username się zgadza I czy token nie wygasł
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // // CZĘŚĆ 3: "Skalpel" - uniwersalna metoda do wyciągania dowolnych danych (Claims)
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // // Najpierw sprawdzamy pieczątkę!
                .build()
                .parseSignedClaims(token) // // Rozcinamy token
                .getPayload(); // // Wyciągamy zawartość "koperty" (Payload)

        return claimsResolver.apply(claims); // // Wykonujemy instrukcję wyciągania konkretnego pola
    }
    // // Pomocnicza metoda sprawdzająca datę przydatności biletu
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}