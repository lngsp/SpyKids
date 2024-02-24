package ro.spykids.server.jwt;
// The following code was written by Bouali Ali and adapted and modity for use in this application.
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import ro.spykids.server.exceptions.InvalidJwtAuthenticationException;
import ro.spykids.server.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String emailFromToken; // the email of the user

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract the JWT Token - remove the first 7 characters
        jwtToken = authHeader.substring(7); // size("Bearer ") = 7
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();

        // extract the email from Jwt Token
        emailFromToken = JwtService.extractEmail(jwtToken);

        // check if user is already authentificated or connected
        if (emailFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // user is not authentificated and we have the email
            // if the SecurityContextHolder returns null => user is not auth

            // Get the user details from BD
            UserDetails userDetails = this.userService.loadUserByUsername(emailFromToken);

            var isTokenValid = tokenRepository.findByValue(jwtToken)
                    .map(token -> !token.isExpired() && !token.isRevoked())
                    .orElse(false);

            // Check if user is valid or not
            if (isTokenValid) {
                if (jwtService.isTokenValid(jwtToken, userDetails)) {

                    // When we created the user, we don't give credentials
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Update the SecurityContext Holder
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new TokenExpiredException("Token has expired", Instant.now());
                }
            } else {
                throw new InvalidJwtAuthenticationException("Invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
