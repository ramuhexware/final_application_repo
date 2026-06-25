package com.rapidx.aggregator.client;

import org.springframework.stereotype.Component;

@Component
public class OAuthTokenCache {
    public String getOAuthAccessToken() {
        return "Bearer sample-oim-sync-token";
    }
}
