package org.pethack.lorby.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.annotations.Comments;
import org.pethack.lorby.model.UserImpDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtCore {
    @Value("${testing.app.secret}")
    private String secret;
    @Value("${testing.app.lifetime}")
    private int lifetime;
    public  String generateToken(Authentication authentication){
        UserImpDetails userImpDetails = (UserImpDetails) authentication.getPrincipal();
        return Jwts.builder().setSubject((userImpDetails.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public String getNameFromJwt(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

}
