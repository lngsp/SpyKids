package ro.spykids.server.jwt;
// The following code was written by Bouali Ali and adapted and modity for use in this application.
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    private static Integer expirationMs = 172800000;
    private static final String Encryption_Key = "614E645267556A586E3272357538782F413F4428472B4B6250655368566D5970";

    public static String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Objects> extractClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))   //token is valide 2 days
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername())) && !isExpired(token);
    }


    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        //get experation Date of the token
        return extractClaim(token, Claims::getExpiration);
    }

    private static Claims extractAllClaims(String token) throws JwtException{
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

    private static Key getSigninKey() {
        byte[] bytes = Decoders.BASE64.decode(Encryption_Key);
        return Keys.hmacShaKeyFor(bytes);
    }


}
