package org.example.models;

import org.example.enums.LoadBalanceAlgo;
import org.example.service.AlgoAssignmnetStartegy;

public class Service {
    private String name;

    private WhitelistingConfig whitelistingConfig;

    private LoadBalanceAlgo loadBalanceAlgo;
    private AlgoAssignmnetStartegy algoAssignmnetStartegy;

    public Integer getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(Integer currentServer) {
        this.currentServer = currentServer;
    }

    private Integer currentServer;

    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    private AuthConfig authConfig;

    public Service(String name, WhitelistingConfig whitelistingConfig, LoadBalanceAlgo loadBalanceAlgo, AuthConfig authConfig) {
        this.name = name;
        this.whitelistingConfig = whitelistingConfig;
        this.loadBalanceAlgo = loadBalanceAlgo;
        this.authConfig = authConfig;
      //  this.algoAssignmnetStartegy = assignStrategy(loadBalanceAlgo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WhitelistingConfig getWhitelistingConfig() {
        return whitelistingConfig;
    }

    public void setWhitelistingConfig(WhitelistingConfig whitelistingConfig) {
        this.whitelistingConfig = whitelistingConfig;
    }

    public LoadBalanceAlgo getLoadBalanceAlgo() {
        return loadBalanceAlgo;
    }

    public void setLoadBalanceAlgo(LoadBalanceAlgo loadBalanceAlgo) {
        this.loadBalanceAlgo = loadBalanceAlgo;
    }
    public boolean validateToken(String token) {
        return this.getAuthConfig().getToken() != null && this.getAuthConfig().getToken().equals(token);
    }
}
