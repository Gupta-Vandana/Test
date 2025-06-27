package org.example.models;

public class Server {
    private String name;

    private String ip;

    private boolean isHealthy;

    private Integer requestsServed;

    public Server(String name, String ip, boolean isHealthy) {
        this.name = name;
        this.ip = ip;
        this.isHealthy = isHealthy;
        this.requestsServed = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean isHealthy) {
        this.isHealthy = isHealthy;
    }

    public Integer getRequestsServed() {
        return requestsServed;
    }

    public void setRequestsServed(int requestsServed) {
        this.requestsServed = requestsServed;
    }
}
