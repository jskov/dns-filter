package dk.mada.dns.web.vaadin;

import java.util.Set;
import java.util.function.Supplier;

import dk.mada.dns.web.app.MainView;

//From https://github.com/Nano-Vaadin-Demos/nano-vaadin-quarkus
public class ServiceFunctions {

	String DEFAULT_PKG_TO_SCAN_PROPERTY = "vaadin-pkg-to-scan";

	static Supplier<Set<Class<?>>> routeClasses() {
		return () -> Set.of(MainView.class);
//		unmodifiableSet(
//				new org.reflections8.Reflections(System.getProperty(DEFAULT_PKG_TO_SCAN_PROPERTY, DEFAULT_PKG_TO_SCAN))
//						.getTypesAnnotatedWith(Route.class));
	}

}
