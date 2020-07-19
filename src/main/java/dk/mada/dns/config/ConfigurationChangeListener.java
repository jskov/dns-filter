package dk.mada.dns.config;

/**
 * Listeners on configuration changes.
 */
public interface ConfigurationChangeListener {
	/**
	 * Called when configuration changes.
	 */
	void configurationChanged();
}