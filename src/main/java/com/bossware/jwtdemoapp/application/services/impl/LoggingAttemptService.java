package com.bossware.jwtdemoapp.application.services.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoggingAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT=1;

    private LoadingCache<String,Integer> loggingAttemptCache;

    public LoggingAttemptService() {
        super();
        loggingAttemptCache= CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(100).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String s) throws Exception {
                return 0;
            }
        });
    }


    public  void evictUserFromLoggingAttemptCache(String username){
        loggingAttemptCache.invalidate(username);
    }

    public  void addUserToLoggingAttemptCache(String username) throws ExecutionException {
        int attempts = 0;
        attempts=ATTEMPT_INCREMENT + loggingAttemptCache.get(username);
        loggingAttemptCache.put(username,attempts);
    }


    public  boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return   loggingAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
    }
}
