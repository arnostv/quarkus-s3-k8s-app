package me.arnost;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.HashMap;

@Path("/datafetch")
public class GreetingResource {

    private final BlobStoreDataFetch blobStoreDataFetch;

    public GreetingResource(BlobStoreDataFetch blobStoreDataFetch) {
        this.blobStoreDataFetch = blobStoreDataFetch;
    }

    @GET
    @Path("/data")
    @Produces(MediaType.TEXT_PLAIN)
    public String fetchData() throws IOException {
        String textData = blobStoreDataFetch.fetchData();
        return "Fetched data: " + textData;
    }

    @GET
    @Path("/whoami")
    @Produces(MediaType.TEXT_PLAIN)
    public String whoami() throws IOException {
        HashMap<Object, Object> callerIdentity = blobStoreDataFetch.callerIdentity();
        return "Identification for data fetch utility: " + callerIdentity;
    }
}
