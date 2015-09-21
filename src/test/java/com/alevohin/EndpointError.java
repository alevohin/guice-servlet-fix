package com.alevohin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by yuriy.alevohin on 21.09.2015.
 * Simple endpoint that always returns 500 Internal error.
 */
@Path("/error")
public class EndpointError {
    @GET
    public String internalError() {
        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
