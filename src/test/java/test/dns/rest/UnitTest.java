package test.dns.rest;

import org.junit.jupiter.api.Test;

import dk.mada.dns.rest.PingResource;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitTest {
	@Test
	public void unitTestingWorks() {
		PingResource sut = new PingResource();
		
		String out = sut.ping();
		assertThat(out)
			.isEqualTo("pong");
	}
}
