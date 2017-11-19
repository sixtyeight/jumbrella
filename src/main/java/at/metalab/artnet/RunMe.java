package at.metalab.artnet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class RunMe {

	public static void main(String[] args) throws Exception {
		final ResourceConfig application = new ResourceConfig().packages("at.metalab.artnet.jaxrs")
				.register(JacksonFeature.class);

		Server server = new Server(9999);

		// JSON processing
		ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(application));
		ServletContextHandler api = new ServletContextHandler(ServletContextHandler.SESSIONS);
		api.setContextPath("/");
		api.addServlet(jerseyServlet, "/*");

		// Serves the static UI
		ContextHandler app = new ContextHandler();
		app.setContextPath("/app");

		ResourceHandler res = new ResourceHandler();
		res.setDirectoriesListed(false);
		res.setWelcomeFiles(new String[] { "index.html" });
		res.setBaseResource(Resource.newClassPathResource("app"));

		app.setHandler(res);

		// register both JSON and static UI
		HandlerCollection context = new HandlerCollection();
		context.addHandler(app);
		context.addHandler(api);

		server.setHandler(context);

		server.start();
		server.join();
	}

}
