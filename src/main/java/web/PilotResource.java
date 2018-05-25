package web;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import model.Pilot;
import model.User;
import org.eclipse.jetty.http.HttpStatus;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import service.PilotService;
import view.DropwizardView;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
@Path("/pilots")
public class PilotResource extends AbstractResource {

    private static final String VIEW_NAME = "pilot.html";

    private final PilotService pilotService;

    public PilotResource(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @GET
    @Timed
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public DropwizardView getPilots(@Auth User user) {
        return getViewAllPilot();
    }

    @POST
    @Timed
    @Path("/new")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("USER")
    public DropwizardView createPilot(@Auth User user, @FormParam("name") String name,
            @FormParam("info") String info,
            @FormParam("level") String level) {
        Pilot pilot = new Pilot();
        pilot.setName(name);
        pilot.setInfo(info);
        pilot.setLevel(level);
        pilotService.createPilot(pilot);

        return getViewAllPilot();
    }

    @POST
    @Timed
    @Path("/edit")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("ADMIN")
    public DropwizardView editPilot(@Auth User user,
            @FormParam("id") Long id,
            @FormParam("name") String name,
            @FormParam("info") String info,
            @FormParam("level") String level) {
        Pilot pilot = pilotService.getPilot(id);
        pilot.setName(name);
        pilot.setInfo(info);
        pilot.setLevel(level);
        pilotService.editPilot(pilot);
        return getViewAllPilot();
    }

    @GET
    @Timed
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilot(@PathParam("id") Long id) {
        return Response.status(HttpStatus.OK_200)
                       .entity(pilotService.getPilot(id))
                       .build();
    }

    @DELETE
    @Timed
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deletePilot(@PathParam("id") Long id) {
        pilotService.deletePilot(id);
        return "{\"message\":\"Deleted\"}";
    }

    private DropwizardView getViewAllPilot() {
        return getView(VIEW_NAME, pilotService.getPilots());
    }
}
