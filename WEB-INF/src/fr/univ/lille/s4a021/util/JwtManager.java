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
        return Jwts.builder()
                .subject("Channel invite")
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .issuedAt(Date.from(Instant.now()))
                .claim("cid", cid)
                .claim("uid", uid)
                .signWith(secret)
                .compact();
    }

    public String createJwtForFriendInvite(Integer senderUid, Integer receiverUid) {
        return Jwts.builder()
                .subject("Friend invite")
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .issuedAt(Date.from(Instant.now()))
                .claim("senderUid", senderUid)
                .claim("receiverUid", receiverUid)
                .signWith(secret)
                .compact();
    }

    public Boolean verifyJWT(String token) {
        try {
            Jwts.parser().verifyWith(secret).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Pair<Integer, Integer> getUidAndCidFromChannelInviteToken(String token) {
        var claims = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getBody();
        return new Pair<>(claims.get("uid", Integer.class), claims.get("cid", Integer.class));
    }

    public Pair<Integer, Integer> getSenderAndReceiverFromFriendInviteToken(String token) {
        var claims = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getBody();
        return new Pair<>(claims.get("senderUid", Integer.class), claims.get("receiverUid", Integer.class));
    }
}
