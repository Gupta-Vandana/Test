package org.example.models;

import java.util.Map;

public class Request {
    private String sourceIp;
    private String requestUrl;
    private String methodType;
    private String requestId;
    private Map<String, String> headers;
    private String authToken;

    public Request(String sourceIp, String requestUrl, String methodType, Map<String, String> headers, String requestId, String authToken) {
        this.sourceIp = sourceIp;
        this.requestUrl = requestUrl;
        this.methodType = methodType;
        this.headers = headers;
        this.requestId = requestId;
        this.authToken = authToken;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
