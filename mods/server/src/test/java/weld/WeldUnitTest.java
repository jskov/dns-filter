package weld;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dk.mada.dns.Environment;

/**
 * Testing that weld-unit is working properly.
 */
@Tag("accept")
@EnableAutoWeld
public class WeldUnitTest {
	// Breaks beans scanning in quarkus 1.3.0.Final due to non-empty beans.xml files
//	@WeldSetup
//    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject private Environment env;
	
	@Test
	void test() {
		assertThat(env)
			.isNotNull();
	}
}
