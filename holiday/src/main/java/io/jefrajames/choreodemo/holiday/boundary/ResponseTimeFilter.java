package io.jefrajames.choreodemo.holiday.boundary;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseTimeFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty("Start-time", System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        Long startTime = (Long) requestContext.getProperty("Start-time");
        if (startTime != null) {
            Long responseTime = System.currentTimeMillis() - startTime;
            responseContext.getHeaders().add("X-Response-Time", responseTime);
        }
    }

}
