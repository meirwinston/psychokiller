package com.psychokiller.ws.resources;

import com.psychokiller.ws.Saying;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/ws")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MainResource {
    @Path("sayHello")
    @GET
    public Saying sayHello() {
        return new Saying(1, "Hello");
    }

}
