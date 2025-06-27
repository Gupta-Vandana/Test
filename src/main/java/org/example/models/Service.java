package org.example.models;

import org.example.enums.LoadBalanceAlgo;

public class Service {
    private String name;

    private WhitelistingConfig whitelistingConfig;

    private LoadBalanceAlgo loadBalanceAlgo;

    public Service(String name, WhitelistingConfig whitelistingConfig, LoadBalanceAlgo loadBalanceAlgo) {
        this.name = name;
        this.whitelistingConfig = whitelistingConfig;
        this.loadBalanceAlgo = loadBalanceAlgo;
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
}
