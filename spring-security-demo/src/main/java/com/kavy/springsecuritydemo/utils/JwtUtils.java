package com.kavy.springsecuritydemo.utils;

import com.kavy.springsecuritydemo.entity.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class JwtUtils {

    private static final String CLAIM_KEY_USER_ID = "user_id";
    private static final String CLAIM_KEY_AUTHORITIES = "scope";

    private Map<String, String> tokenMap = new ConcurrentHashMap<>(32);

    //密匙
    @Value("${jwt.secret}")
    private String secret;
    //有效期
    @Value("${jwt.expiration}")
    private Long access_token_expiration;

    //刷新密匙
    @Value("${jwt.expiration}")
    private Long refresh_token_expiration;

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;


    //获取token
    public String generateAccessToken(UserDetail userDetail) {
        Map<String, Object> claims = generateClaims(userDetail);
        claims.put(CLAIM_KEY_AUTHORITIES, authoritiesToArray(userDetail.getAuthorities()).get(0));
        return generateAccessToken(userDetail.getUsername(), claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public void putToken(String userName, String token) {
        tokenMap.put(userName, token);
    }


    /**
     * 生成token的过期时间
     * @return
     */
    private Date generateExpirationDate(long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 从token中获取claims
     *
     **/
    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     *  获取用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    /**
     *  获取token 过期时间
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     *     判断token 是否过期
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     *  判断token是否可以刷新
     * @param token
     * @return
     */
    public Boolean canTokenBeRefreshed(String token) {
        final Date created = getCreatedDateFromToken(token);
        return !isTokenExpired(token);
    }

    /**
     * 从token中获取创建时间
     * @param token
     * @return
     */
    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = claims.getIssuedAt();
        } catch (Exception e) {
            created = null;
        }
        return created;
    }
    /**
     *   刷新token
     * @param token
     * @return
     */

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            refreshedToken = generateAccessToken(claims.getSubject(), claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }


    /**
     *  claims
     * @param userDetail
     * @return
     */
    private Map<String, Object> generateClaims(UserDetail userDetail) {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put(CLAIM_KEY_USER_ID, userDetail.getId());
        System.out.println("userDetail.getId()--------->>>"+userDetail.getId());
        System.out.println("userDetail--->"+userDetail.toString());
        return claims;
    }

    // 传入 username 过期时间 claims 获取token
    private String generateAccessToken(String subject, Map<String, Object> claims) {
        // subject  --->userDetail.getUsername()  access_token_expiration过期时间
        return generateToken(subject, claims, access_token_expiration);
    }


    private List authoritiesToArray(Collection<? extends GrantedAuthority> authorities) {
        List<String> list = new ArrayList<>();
        for (GrantedAuthority ga : authorities) {
            list.add(ga.getAuthority());
        }
        return list;
    }



    /**
     *      生成token
     * @param subject
     * @param claims
     * @param expiration
     * @return
     */
    private String generateToken(String subject, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) //username  设置面向用户
               // .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())  //签发时间
                .setExpiration(generateExpirationDate(expiration)) //生成token的过期时间
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SIGNATURE_ALGORITHM, secret)  //SignatureAlgorithm.HS512, secret 生成签名
                .compact();
    }

}