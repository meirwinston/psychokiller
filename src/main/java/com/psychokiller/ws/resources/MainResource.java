package com.psychokiller.ws.resources;

import com.psychokiller.wire.messages.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MainResource {

//    http://localhost:8080/v1/test
    @Path("test")
    @GET
    public Saying test() {
        return new Saying("Hello");
    }

}
