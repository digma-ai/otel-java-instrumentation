package org.digma.otel.test.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class JaxrsDemo {

    @GET
    @Path("hello")
    public String hello(){
        return "Hello";
    }
}
