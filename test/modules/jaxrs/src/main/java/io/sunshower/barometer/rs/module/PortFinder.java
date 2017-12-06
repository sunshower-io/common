package io.sunshower.barometer.rs.module;

import io.sunshower.barometer.jaxrs.Port;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by haswell on 10/25/16.
 */
public class PortFinder {

    public static int freePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }

    public static int resolve(Port port) {
        switch(port.mode()) {
            case Dynamic:
                return freePort();
            default:
                return port.value();
        }
    }
}
