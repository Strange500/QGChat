package fr.univ.lille.s4a021.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtManager {

    public static SecretKey secret = Jwts.SIG.HS256.key().build();


    public String createJwtForChannelLink(Integer uid, Integer cid) {
        String token = Jwts.builder()
                .subject("Channel invite")
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issuedAt(Date.from(Instant.now()))
                .claim("cid", cid)
                .claim("uid", uid)
                .signWith(secret)
                .compact();
        return token;
    }

    public Boolean verifyJWT(String token) {
        try {
            Jwts.parser().verifyWith(secret).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Pair<Integer,Integer> getUidAndCidFromChannelInviteToken(String token) {

        Integer uid = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getBody().get("uid", Integer.class);
        Integer cid = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getBody().get("cid", Integer.class);
        return new Pair<>(uid, cid);

    }
}
