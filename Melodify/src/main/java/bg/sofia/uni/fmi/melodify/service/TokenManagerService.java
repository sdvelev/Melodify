package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenManagerService {
    private final static String SECRET_KEY = "e2908e6612bab17d8904e9f4d2762d75b95379d8742ef5c7895a201818c3d64f";
    private final static long EXPIRATION_IN_MILLIS = 86_400_000;
    private final static String PAYLOAD_FIELD_USER_ID = "userId";
    private final static String PAYLOAD_FIELD_IS_ADMIN = "isAdmin";


    public String generateTokenByUser(User userForToken, boolean isAdmin) {

        return Jwts.builder()
            .addClaims(generatePayloadByUser(userForToken, isAdmin))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    public String getUserIdFromToken(String tokenToDecode) {

        Claims claims = null;
        try {
            claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(tokenToDecode)
                .getBody();
        } catch (Exception e) {
            throw new MethodNotAllowed("There is a problem with the authentication process");
        }

        return claims.get(PAYLOAD_FIELD_USER_ID).toString();
    }

    public String getIsAdminFromToken(String tokenToDecode) {

        Claims claims = null;
        try {
            claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(tokenToDecode)
                .getBody();
        } catch (Exception e) {
            throw new MethodNotAllowed("There is a problem with the authentication process");
        }

        return claims.get(PAYLOAD_FIELD_IS_ADMIN).toString();
    }

    private Claims generatePayloadByUser(User userForToken, boolean isAdmin) {

        Claims payloadByUser = Jwts.claims();
        payloadByUser.put(PAYLOAD_FIELD_USER_ID, userForToken.getId());
        payloadByUser.put(PAYLOAD_FIELD_IS_ADMIN, isAdmin);

        Date currentTime = new Date(System.currentTimeMillis());
        payloadByUser.setIssuedAt(currentTime);

        Date expirationTime = new Date(currentTime.getTime() + EXPIRATION_IN_MILLIS);
        payloadByUser.setExpiration(expirationTime);

        return payloadByUser;
    }
}