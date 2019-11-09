package dk.mada.dns.web.vaadin;

import org.jboss.jandex.DotName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.router.Route;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.undertow.deployment.ServletBuildItem;

// From https://github.com/Nano-Vaadin-Demos/nano-vaadin-quarkus
public class VaadinProcessor {
	private static final Logger logger = LoggerFactory.getLogger(VaadinProcessor.class);
	
	private static DotName routeAnnotation = DotName.createSimple(Route.class.getName());

	 @BuildStep
	  FeatureBuildItem featureBuildItem() {
	    logger.info("Add Feature For Vaadin");
	    return new FeatureBuildItem("quarkus-vaadin");
	  }

	  @BuildStep
	  BeanDefiningAnnotationBuildItem registerRouteAnnotation() {
	    logger.info("Add Feature For Vaadin - registerRouteAnnotation");
	    return new BeanDefiningAnnotationBuildItem(routeAnnotation);
	  }

	  @BuildStep
	  AdditionalBeanBuildItem registerVaadinServlet() {
	    logger.info("Add Feature For Vaadin - registerVaadinServlet");
	    //TODO make it dynamic
	    return new AdditionalBeanBuildItem(QuarkusVaadinServlet.class);
	  }

	  @BuildStep
	  ServletBuildItem vaadinServletBuildItem() {
	    logger.info("Add Feature For Vaadin - vaadinServletBuildItem");
	    //TODO make it dynamic
	    return ServletBuildItem.builder(QuarkusVaadinServlet.class.getSimpleName(), QuarkusVaadinServlet.class.getName())
	                           .addMapping("/vaadin/*")
	                           .addMapping("/frontend/*")
	                           .build();
	  }

	  @BuildStep
	  void scanForBeans(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
	    logger.info("Add Feature For Vaadin - scanForBeans");
	    ServiceFunctions.routeClasses().get()
	                  .stream()
	                  .map(c -> c.getAnnotation(Route.class))
	                  .map(Route::value)
	                  .peek(v -> logger.info("add value (Route) - " + v))
	                  .forEach(value -> reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, value)));

	  }
}
