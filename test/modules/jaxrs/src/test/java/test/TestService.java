package test;

import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by haswell on 10/25/16.
 */
@Service
@Path("cool")
public interface TestService {
    @GET
    @Path("/bean/{cool}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    String sayHelloWithInjection(@PathParam("cool") String name);

    @GET
    @Path("/{cool}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    String sayHello(@PathParam("cool") String name);

    @GET
    @Path("/{cool}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    String sayHelloXML(@PathParam("cool") String name);

}
