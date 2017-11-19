package at.metalab.artnet.jaxrs;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;

@Path("/api")
public class SetColorApplication {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/color")
	public Response setColor(Color color) {
		if (color == null) {
			return Response.status(HttpStatus.BAD_REQUEST_400).build();
		}

		try {
			ArtnetPackets.sendMainRoom(color.r, color.g, color.b, 0, 0);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/white")
	public Response setWhite(White white) {
		if (white == null) {
			return Response.status(HttpStatus.BAD_REQUEST_400).build();
		}

		try {
			ArtnetPackets.sendMainRoom(0, 0, 0, white.amber, white.cold);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	public static class Color {
		public int r;

		public int g;

		public int b;
	}

	public static class White {
		public int amber;

		public int cold;
	}
}
