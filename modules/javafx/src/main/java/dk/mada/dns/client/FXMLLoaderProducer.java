package dk.mada.dns.client;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

public class FXMLLoaderProducer {
	@Inject
	private Instance<Object> instance;

	@Produces
	public FXMLLoader createLoader() {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(new Callback<Class<?>, Object>() {
			@Override
			public Object call(Class<?> param) {
				return instance.select(param).get();
			}
		});
		return loader;
	}
}