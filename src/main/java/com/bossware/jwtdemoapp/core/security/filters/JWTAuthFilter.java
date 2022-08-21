package com.bossware.jwtdemoapp.core.security.filters;

import com.bossware.jwtdemoapp.core.constants.SecurityConstants;
import com.bossware.jwtdemoapp.core.utilities.JWTProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private JWTProvider jwtProvider;

    public JWTAuthFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getMethod().equals(SecurityConstants.OPTIONS_HTTP_METHOD)){
            response.setStatus(HttpStatus.OK.value());
        }else{
            String autharizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(autharizationHeader)||!autharizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)){
                filterChain.doFilter(request,response);
            }

            String token = autharizationHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
            String userName = jwtProvider.getSubject(token);
            if(jwtProvider.isTokenValid(userName,token)&& SecurityContextHolder.getContext().getAuthentication()==null){
                List<GrantedAuthority> authorityList = jwtProvider.getAuthorities(token);
                Authentication authentication = jwtProvider.getAuthentication(userName,authorityList,request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request,response);
    }
}
