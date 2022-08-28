package com.bossware.jwtdemoapp.core.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bossware.jwtdemoapp.core.constants.SecurityConstants;
import com.bossware.jwtdemoapp.core.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateJWTToken(UserPrincipal userPrincipal){
        String[] claims  = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(SecurityConstants.GET_BOSSWARE_ISSUER)
                .withAudience(SecurityConstants.GET_ARRAYS_AUDIENCE)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(SecurityConstants.AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))

                .sign(Algorithm.HMAC512(secret.getBytes()))
                ;
    }

    public List<GrantedAuthority> getAuthorities (String token){
        String[] claimsOfUser = getClaimsFromToken(token);
        return Arrays.stream(claimsOfUser).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String userName , List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName,null,authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }

    public boolean isTokenValid(String userName,String token){
        JWTVerifier verifier = getJWTVerifier();
        return  StringUtils.hasText(userName) && isTokenVerified(verifier,token);
    }

    public String getSubject(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenVerified(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return jwtVerifier.verify(token).getClaim(SecurityConstants.AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try{
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier= JWT.require(algorithm).withIssuer(SecurityConstants.GET_BOSSWARE_ISSUER).build();
        }catch (JWTVerificationException ex){
            throw new JWTVerificationException(SecurityConstants.TOKEN_CANNOT_BE_VERIFIED);
        }
        return  verifier;
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal){
        List<String> authoritiesList = new ArrayList<>();
        for(GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()){
            authoritiesList.add(grantedAuthority.getAuthority());
        }
        return authoritiesList.toArray(new String[0]);
    }

}
