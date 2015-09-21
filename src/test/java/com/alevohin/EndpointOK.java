package com.alevohin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by yuriy.alevohin on 21.09.2015.
 * Simple endpoint that always returns "OK".
 */
@Path("ok")
public class EndpointOK {
    @GET
    public String success() {
        return "OK";
    }
}
