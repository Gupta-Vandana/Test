package org.example.helper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class RequestHelper {

    private RequestHelper() {

    }

    public static Optional<String> getServiceName(String requestUrl) {
        try {
            URL url = new URL(requestUrl);
            String host = url.getHost(); // Gets the full host like wingman.abc.net or abc.def.ghi
            String[] subdomains = host.split("\\."); // Split by dot

            // Return the first part of the subdomain, which can be considered the service name
            return Optional.of(subdomains[0]); // Service name is typically the first subdomain
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL: " + requestUrl);
            return Optional.empty();
        }
    }
}
