package org.example.util;

public class IpUtils {

    private static final int IPV4_PART_COUNT = 4;

    private IpUtils() {

    }

    public static boolean validateIpAddress(String ipString) {
        int[] bytes = parseIpAddress(ipString);
        return bytes.length == IPV4_PART_COUNT;
    }

    public static int[] parseIpAddress(final String ipString) {
        String[] ipParts = ipString.split("://");
        final String protocol = ipParts[0];
        if(!protocol.equals("http") && !protocol.equals("https")){
            return new int[0];
        }
        String ipAddress = ipParts[1];
        String[] address = ipAddress.split("\\.", IPV4_PART_COUNT + 1);
        if (address.length != IPV4_PART_COUNT) {
            return new int[0];
        }

        int[] bytes = new int[IPV4_PART_COUNT];
        try {
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = parseOctet(address[i]);
            }
        } catch (NumberFormatException ex) {
            return new int[0];
        }
        return bytes;
    }

    private static int parseOctet(String ipPart) {
        int octet = Integer.parseInt(ipPart);
        if (octet > 255 || (ipPart.startsWith("0") && ipPart.length() > 1)) {
            throw new NumberFormatException();
        }
        return octet;
    }
}
