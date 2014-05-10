package io.undertow.examples;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.examples.servlet.MessageServlet;
import io.undertow.examples.servlet.ServletServer;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.ServletException;

public class Runner {

	public static final String MYAPP = "/myapp";

	public static void main(final String[] args) {
		try {
			DeploymentInfo servletBuilder = deployment()
					.setClassLoader(ServletServer.class.getClassLoader())
					.setContextPath(MYAPP)
					.setDeploymentName("test.war")
					.addServlets(
							servlet("MessageServlet", MessageServlet.class)
									.addInitParam("message", "Hello World")
									.addMapping("/*"),
							servlet("MyServlet", MessageServlet.class)
									.addInitParam("message", "MyServlet")
									.addMapping("/myservlet"));

			DeploymentManager manager = defaultContainer().addDeployment(
					servletBuilder);
			manager.deploy();

			HttpHandler servletHandler = manager.start();
			PathHandler path = Handlers.path(Handlers.redirect(MYAPP))
					.addPrefixPath(MYAPP, servletHandler);
			Undertow server = Undertow.builder().addListener(8080, "localhost")
					.setHandler(path).build();
			server.start();
			System.out.println("ok");
//			server.stop();
//			System.exit(0);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}
}
