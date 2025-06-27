package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.example.enums.LoadBalanceAlgo;
import org.example.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatewayLoadBalancerTests {

    // Constants
    private static final String SERVICE_B_TOKEN = "service_b_token";
    private static final String SERVICE_A_TOKEN = "service_a_token";

    private static final String REQUEST1_IP1 = "http://192.0.0.1";

    private static final String REQUEST_1_ID = "ebc45c97-1d9d-4c24-9d7d-8a8a6e740b72";

    private static final String SERVER_1_IP = "http://130.0.0.1";
    private static final String SERVER_2_IP = "http://130.0.0.2";
    private static final String SERVER_3_IP = "http://130.0.0.3";


    // Instances
    private Gateway gateway;

    private Service serviceA;
    private Service serviceB;
    private Service serviceC;

    private Server server1;
    private Server server2;
    private Server server3;

    @BeforeEach
    void setUp() {
        buildServices();
        buildServers();

        // TODO: Implement gateway GatewayImpl()
        // add dependencies for gateway
        gateway = new GatewayImpl();
    }

    private void buildServers() {
        server1 = new Server("Server 1", SERVER_1_IP, true);
        server2 = new Server("Server 2", SERVER_2_IP, true);
        server3 = new Server("Server 3", SERVER_3_IP, true);
    }

    private void buildServices() {
        serviceA = new Service("serviceA", buildEnableAllWhiteListingConfig(), LoadBalanceAlgo.ROUND_ROBIN, buildEnableAuthConfigConfigForServiceA());
        serviceC = new Service("serviceC", buildEnableAllWhiteListingConfig(), LoadBalanceAlgo.ROUND_ROBIN, buildEnableAuthConfigConfigForServiceB());
        serviceB = new Service("serviceB", buildWhiteListingConfigForServiceB(), LoadBalanceAlgo.LEAST_REQUESTS, buildDisableAuthConfigConfigForServiceB());
    }

    @Test
    void testRegisterServiceShouldReturnTrue() {
        assertTrue(gateway.registerService(serviceA));
    }

    @Test
    void testAlreadyRegisteredServiceShouldReturnFalse() {
        gateway.registerService(serviceA);
        assertFalse(gateway.registerService(serviceA));
    }

    @Test
    void testDeRegisterServiceShouldReturnTrue() {
        gateway.registerService(serviceA);
        assertTrue(gateway.deRegisterService(serviceA));
    }

    @Test
    void testDeRegisterServiceAlreadyDeRegisteredShouldReturnFalse() {
        gateway.registerService(serviceA);
        assertTrue(gateway.deRegisterService(serviceA));
        assertFalse(gateway.deRegisterService(serviceA));
    }

    @Test
    void testDeRegisterServiceForUnRegisteredShouldReturnFalse() {
        assertFalse(gateway.deRegisterService(serviceA));
    }


    @Test
    void testAddServerToServiceShouldReturnTrue() {
        gateway.registerService(serviceA);
        boolean added = gateway.addServer(server1, serviceA.getName());
        assertTrue(added);
    }

    @Test
    void testRemoveServerToServiceShouldReturnTrue() {
        gateway.registerService(serviceA);
        gateway.addServer(server1, serviceA.getName());
        boolean serverRemoved = gateway.removeServer(server1, serviceA.getName());
        assertTrue(serverRemoved);
    }

    @Test
    void testRemoveNonExistingServerShouldReturnFalse() {
        gateway.registerService(serviceA);
        boolean serverRemoved = gateway.removeServer(server1, serviceA.getName());
        assertFalse(serverRemoved);
    }

    @Test
    void testExistingServerAdditionToServiceShouldReturnFalse() {
        gateway.registerService(serviceA);
        gateway.addServer(server1, serviceA.getName());
        boolean added = gateway.addServer(server1, serviceA.getName());
        assertFalse(added);
    }

    @Test
    void testRequestToUnknownServiceShouldReturn400() {
        Request request = new Request("http://127.0.0.1", "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, null);
        Response response = gateway.processRequest(request);
        assertNotNull(response);
        assertEquals(400, response.getHttpStatusCode());
    }

    @Test
    void testRequestWithoutSourceIpShouldReturn400() {
        Request request = new Request(null, "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, null);
        gateway.registerService(serviceB);
        Response response = gateway.processRequest(request);
        assertNotNull(response);
        assertEquals(400, response.getHttpStatusCode());
    }

    @Test
    void testRequestWithoutRequestUrlShouldReturn400() {
        Request request = new Request("http://127.0.0.1", null, "POST",
                null, REQUEST_1_ID, null);
        gateway.registerService(serviceB);
        Response response = gateway.processRequest(request);
        assertNotNull(response);
        assertEquals(400, response.getHttpStatusCode());
    }

    @Test
    void testRequestWithInvalidHostInIpShouldReturn400() {
        Request request = new Request("htp://255.0.0.1", "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, null);
        gateway.registerService(serviceB);
        Response response = gateway.processRequest(request);
        assertNotNull(response);
        assertEquals(400, response.getHttpStatusCode());
    }

    @Test
    void testRequestWithInvalidIpShouldReturn400() {
        Request request = new Request("http://256.0.0.1", "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, SERVICE_B_TOKEN);
        gateway.registerService(serviceB);
        Response response = gateway.processRequest(request);
        assertNotNull(response);
        assertEquals(400, response.getHttpStatusCode());
    }

    @Test
    void testRequestFromAnUnAuthorizedIpShouldReturn403() {
        gateway.registerService(serviceB);
        Request request = new Request("http://127.0.0.1", "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, null);
        Response response = gateway.processRequest(request);
        assertEquals(403, response.getHttpStatusCode());
    }

    @Test
    void testRequestWithNoServersAvailableShouldReturn503() {
        gateway.registerService(serviceA);
        Request request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/products", "POST",
                null, REQUEST_1_ID, SERVICE_A_TOKEN);
        Response response = gateway.processRequest(request);
        assertEquals(503, response.getHttpStatusCode());
    }

    @Test
    void testRequestWithMultipleServersWithRoundRobin() {
        gateway.registerService(serviceA);
        gateway.addServer(server1, serviceA.getName());
        gateway.addServer(server2, serviceA.getName());

        // Req-1
        Request request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/products", "POST",
                null, REQUEST_1_ID, SERVICE_A_TOKEN);
        Response response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-1, server 1 should be selected");

        // Req-2
        request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/skus", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_2_IP, response.getServerAssigned(), "For Req-2, server 2 should be selected");

        // Req-3
        request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/services", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-3, server 1 should be selected");
    }

    @Test
    void testRequestWithMultipleServersDeRegisterAndRegisterWithRoundRobin() {
        gateway.registerService(serviceA);
        gateway.addServer(server1, serviceA.getName());
        gateway.addServer(server2, serviceA.getName());

        // Req-1
        Request request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/products", "POST",
                null, REQUEST_1_ID, SERVICE_A_TOKEN);
        Response response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-1, server 1 should be selected");

        gateway.removeServer(server2, serviceA.getName());

        // Req-2
        request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/skus", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-2, server 1 should be selected");

        gateway.addServer(server2, serviceA.getName());

        // Req-3
        request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/services", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_2_IP, response.getServerAssigned(), "For Req-3, server 2 should be selected");
    }

    @Test
    void testRequestWithMultipleServersAndMultipleServicesWithRoundRobin() {
        gateway.registerService(serviceA);
        gateway.registerService(serviceC);

        gateway.addServer(server1, serviceA.getName());
        gateway.addServer(server2, serviceA.getName());
        gateway.addServer(server3, serviceC.getName());

        // Req-1
        Request request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/products", "POST",
                null, REQUEST_1_ID, null);
        Response response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-1, server 1 should be selected");

        // Req-2
        request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/skus", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_2_IP, response.getServerAssigned(), "For Req-2, server 2 should be selected");

        // Req-3
        // check if service C Ip should be provided
        request = new Request(REQUEST1_IP1, "http://serviceC.cred.com/skus", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_3_IP, response.getServerAssigned(), "For Req-3, server 3 should be selected");

        // Req-4
        request = new Request(REQUEST1_IP1, "http://serviceA.cred.com/services", "POST",
                null, REQUEST_1_ID, null);
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-4, server 1 should be selected");
    }

    @Test
    void testRequestWithNoServersAvailableWithLeastConnections() {
        gateway.registerService(serviceB);
        Request request = new Request(REQUEST1_IP1, "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, SERVICE_B_TOKEN);
        Response response = gateway.processRequest(request);
        assertEquals(503, response.getHttpStatusCode());
        assertNull(response.getServerAssigned());
    }

    @Test
    void testRequestsWithServersAvailableWithLeastConnections() {
        gateway.registerService(serviceB);
        gateway.addServer(server1, serviceB.getName());
        gateway.addServer(server2, serviceB.getName());

        // Req-1
        Request request = new Request(REQUEST1_IP1, "http://serviceB.cred.com/products", "POST",
                null, REQUEST_1_ID, SERVICE_B_TOKEN);
        Response response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-1, server 1 should be selected");

        // Req-2
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_2_IP, response.getServerAssigned(), "For Req-2, server 2 should be selected");

        // Req-3
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-3, server 1 should be selected");

        // Req-4
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_2_IP, response.getServerAssigned(), "For Req-4, server 2 should be selected");

        gateway.addServer(server3, serviceB.getName());
        // Req-5
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_3_IP, response.getServerAssigned(), "For Req-5, server 3 should be selected");

        // Req-6
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_3_IP, response.getServerAssigned(), "For Req-6, server 3 should be selected");

        // Req-7
        response = gateway.processRequest(request);
        assertEquals(200, response.getHttpStatusCode());
        assertEquals(SERVER_1_IP, response.getServerAssigned(), "For Req-7, server 1 should be selected");
    }

    private WhitelistingConfig buildEnableAllWhiteListingConfig() {
        return new WhitelistingConfig(true, Arrays.asList("*"));
    }

    private WhitelistingConfig buildWhiteListingConfigForServiceB() {
        return new WhitelistingConfig(true, Arrays.asList(REQUEST1_IP1));
    }

    private AuthConfig buildEnableAuthConfigConfigForServiceB() {
        return new AuthConfig(true, "service_b_token");
    }
    private AuthConfig buildDisableAuthConfigConfigForServiceB() {
        return new AuthConfig(false, "service_b_token");
    }
    private AuthConfig buildEnableAuthConfigConfigForServiceA() {
        return new AuthConfig(true, SERVICE_A_TOKEN);
    }

    private AuthConfig buildDisableAuthConfigConfig() {
        return new AuthConfig(true, null);
    }
}
