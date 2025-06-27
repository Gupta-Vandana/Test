package org.example.models;

import java.util.ArrayList;
import java.util.List;


public class AuthConfig {
    private boolean enabled;
    private String token;

    public AuthConfig(boolean enabled, String token) {
        this.enabled = enabled;
        this.token = token;
    }

    public AuthConfig() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

