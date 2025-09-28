package com.example.demo.config;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class PortRestrictionFilter implements Filter {

    private static final int RESTRICTED_PORT = 8081;
    private static final String RESTRICTED_PATH = "/api/restricted"; // <-- Your single endpoint path

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        int localPort = httpRequest.getLocalPort();
        String requestURI = httpRequest.getRequestURI();

        // Check if the request is for the RESTRICTED_PORT
        if (localPort == RESTRICTED_PORT) {
            // If on the restricted port, ONLY allow the restricted path
            if (requestURI.startsWith(RESTRICTED_PATH)) {
                chain.doFilter(request, response); // Allow access
            } else {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied on this port.");
            }
        } else {
            // If on the main port (e.g., 8080), block the restricted path
            if (requestURI.startsWith(RESTRICTED_PATH)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied on the main port.");
            } else {
                chain.doFilter(request, response); // Allow all other paths
            }
        }
    }
    // other Filter methods (init, destroy) can be empty
}