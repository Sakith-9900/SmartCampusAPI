package com.smartcampus.smartcampusapi;

import com.smartcampus.smartcampusapi.exception.GlobalExceptionMapper;
import com.smartcampus.smartcampusapi.exception.LinkedResourceNotFoundMapper;
import com.smartcampus.smartcampusapi.exception.RoomNotEmptyMapper;
import com.smartcampus.smartcampusapi.exception.SensorUnavailableMapper;
import com.smartcampus.smartcampusapi.filter.LoggingFilter;
import com.smartcampus.smartcampusapi.resource.DiscoveryResource;
import com.smartcampus.smartcampusapi.resource.RoomResource;
import com.smartcampus.smartcampusapi.resource.SensorResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application entry point.
 * Sets the base URI for all API endpoints to /api/v1
 * Manually registers all resource classes, exception mappers and filters.
 */

@ApplicationPath("/api/v1")
public class JAXRSConfiguration extends Application {
    
     /**
     * Registers all JAX-RS components explicitly.
     * This ensures Jersey finds every resource, mapper and filter.
     */
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Resources
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        // Exception Mappers
        classes.add(RoomNotEmptyMapper.class);
        classes.add(LinkedResourceNotFoundMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(GlobalExceptionMapper.class);
        // Filters
        classes.add(LoggingFilter.class);
        return classes;
    }
}
