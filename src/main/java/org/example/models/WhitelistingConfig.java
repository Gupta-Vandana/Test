package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class WhitelistingConfig {
    private boolean enabled;
    private List<String> whitelistIps;

    public WhitelistingConfig(boolean enabled, List<String> whitelistIps) {
        this.enabled = enabled;
        this.whitelistIps = whitelistIps;
    }

    public WhitelistingConfig() {
        this.enabled = false;
        this.whitelistIps = new ArrayList<>();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getWhitelistIps() {
        return whitelistIps;
    }

    public void setWhitelistIps(List<String> whitelistIps) {
        this.whitelistIps = whitelistIps;
    }
}
