package org.example.service;

import org.example.models.Request;
import org.example.models.Response;
import org.example.models.Server;
import org.example.models.Service;

public interface Gateway {
    boolean registerService(Service service);

    boolean deRegisterService(Service service);

    boolean addServer(Server server, String serviceName);

    boolean removeServer(Server server, String serviceName);

    Response processRequest(Request request);
}
