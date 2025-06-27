package org.example.models;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int httpStatusCode;

    private String serverAssigned;

    private Map<String, String> headers = new HashMap<>();

    private Object body;

    public Response() {

    }

    public Response(int httpStatusCode, String body) {
        this.httpStatusCode = httpStatusCode;
        this.body = body;
    }

    public Response(int httpStatusCode, String serverAssigned, Object body) {
        this.body = body;
        this.serverAssigned = serverAssigned;
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getServerAssigned() {
        return serverAssigned;
    }

    public void setServerAssigned(String serverAssigned) {
        this.serverAssigned = serverAssigned;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
