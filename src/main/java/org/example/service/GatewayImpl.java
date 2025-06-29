package org.example.service;

import org.example.enums.LoadBalanceAlgo;
import org.example.models.*;

import java.util.*;

public class GatewayImpl implements Gateway {

    private final Map<String, Service> services = new HashMap<>();
    private final Map<String, Server> ipToServer = new HashMap<>();
    private final Map<String, List<Server>> serviceToServers = new HashMap<>();

    @Override
    public boolean registerService(Service service) {
        if (services.containsKey(service.getName())) return false;
        services.put(service.getName(), service);
        serviceToServers.put(service.getName(), new ArrayList<>());
        return true;
    }

    @Override
    public boolean deRegisterService(Service service) {
        if (!services.containsKey(service.getName())) return false;


        List<Server> servers = serviceToServers.get(service.getName());
        if (servers != null) {
            for (Server server : servers) {
                ipToServer.remove(server.getIp());
            }
        }

        services.remove(service.getName());
        serviceToServers.remove(service.getName());
        return true;
    }

    @Override
    public boolean addServer(Server server, String serviceName) {
        if (ipToServer.containsKey(server.getIp())) return false;
        Service service = services.get(serviceName);
        if (service == null) return false;

        synchronized (server) {
            ipToServer.put(server.getIp(), server);
            serviceToServers.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(server);
        }
        return true;
    }

    @Override
    public boolean removeServer(Server server, String serviceName) {
        if (!ipToServer.containsKey(server.getIp())) return false;

        List<Server> servers = serviceToServers.get(serviceName);
        if (servers != null) {
            servers.removeIf(s -> s.getIp().equals(server.getIp()));
        }

        ipToServer.remove(server.getIp());
        return true;
    }

    @Override
    public Response processRequest(Request request) {

        // 1. Request validation
        if (request.getRequestUrl() == null || request.getRequestUrl().isEmpty()) {
            return new Response(400, "Invalid request URL", null);
        }
        if (request.getSourceIp() == null || request.getSourceIp().isEmpty()) {
            return new Response(400, "Missing source IP", null);
        }


        String url = request.getRequestUrl();
        String domainPart = url.replace("http://", "").replace("https://", "").split("/")[0];
        String[] domainTokens = domainPart.split("\\.");
        if (domainTokens.length == 0) {
            return new Response(400, "Invalid domain in URL", null);
        }
        String serviceName = domainTokens[0];


        Service service = services.get(serviceName);
        if (service == null) {
            return new Response(400, "Service not found", null);
        }


        if (service.getAuthConfig().isEnabled()) {
            String token = request.getAuthToken();
            if (token == null || !service.validateToken(token)) {
                return new Response(403, "Invalid or missing Auth Token", null);
            }
        }

        if (service.getWhitelistingConfig().isEnabled()) {
            List<String> whitelistedIps = service.getWhitelistingConfig().getWhitelistIps();
            if (!whitelistedIps.contains(request.getSourceIp())) {
                return new Response(403, "Source IP not whitelisted", null);
            }
        }

        List<Server> availableServers = serviceToServers.get(serviceName);
        if (availableServers == null || availableServers.isEmpty()) {
            return new Response(503, "No servers available for service: " + serviceName, null);
        }

        Server assignedServer;

        if (Objects.isNull(service.getCurrentServer())) {
            assignedServer = availableServers.get(0);
            assignedServer.setRequestsServed(assignedServer.getRequestsServed() + 1);
            service.setCurrentServer(0);
            return new Response(200, "Request [" + request.getRequestId() + "] to " +
                    serviceName + " served by IP: " + assignedServer.getIp(), assignedServer.getIp());

        }

        if (service.getLoadBalanceAlgo().equals(LoadBalanceAlgo.ROUND_ROBIN))
            return assignServerByGivenAlgo(service.getLoadBalanceAlgo(), service, request);
        else {
            return assignServerByLeastConnection(service.getLoadBalanceAlgo(), service, request);
        }

    }

    //ROUND ROBIN
    private Response assignServerByGivenAlgo(LoadBalanceAlgo algo, Service service, Request request) {

        List<Server> availableServers = serviceToServers.get(service.getName());


        int nextServer = service.getCurrentServer() + 1;
        if (nextServer > availableServers.size()) {

            Server server1 = availableServers.get(0);
            service.setCurrentServer(0);
            return new Response(200, "Request [" + request.getRequestId() + "] to " +
                    service.getName() + " served by IP: " + server1.getIp(), server1.getIp());

        }

        Server server = availableServers.get(nextServer);
        service.setCurrentServer(0);
        return new Response(200, "Request [" + request.getRequestId() + "] to " +
                service.getName() + " served by IP: " + server.getIp(), server.getIp());
    }

    //Least Connection
    private Response assignServerByLeastConnection(LoadBalanceAlgo algo, Service service, Request request) {

        List<Server> availableServers = serviceToServers.get(service.getName());

        Server leastConnectedServer = availableServers.get(0);
        int leastConnections = Integer.MAX_VALUE;
        for (Server server : availableServers) {
            if (server.getRequestsServed() < leastConnections) {
                leastConnectedServer = server;
            }
        }
        return new Response(200, "Request [" + request.getRequestId() + "] to " +
                service.getName() + " served by IP: " + leastConnectedServer.getIp(), leastConnectedServer.getIp());


    }
}
