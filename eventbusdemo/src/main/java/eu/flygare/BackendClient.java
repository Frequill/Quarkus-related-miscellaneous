/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.flygare;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.logging.Logger;

/**
 *
 * @author flax
 */
@Path("/mypath")
@RegisterRestClient(configKey = "backend-api") // configkey är så propertiesfilen blir enklare att skriva.
public interface BackendClient {

    // Notera att vi bara specar ett interface, men med samma signatur som i backend, dvs /mypath/login, GET/POST, samt typerna.
    // Quarkus kommer att skapa "rätt" anrop för det. 
    // Vi lägger in vart anropet ska skickas i properties-filen
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> getHello();

    // Path to this method will be /mypath/login since Path annotations "add up".
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginTokenEntity postHello(LoginEntity logindata);
    
    // Call non-existing method on server to trigger error
    @GET
    @Path("/error")
    public void doError();


    // Basic error handling on status!=200...
    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        Logger.getLogger("restclient").error("Exception handling");
        switch (response.getStatus()) {
            case 500 -> {
                return new RuntimeException("The remote service responded with HTTP 500");
            }
            case 404 -> {
                return new RuntimeException("The remote service responded with HTTP 404");
            }
        }
       
        return null;
    }

}
