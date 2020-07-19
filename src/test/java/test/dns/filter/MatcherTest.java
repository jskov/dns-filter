package test.dns.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.HostDomainNameMatcher;

/**
 * Tests host/domain name matching.
 * Host names are matches in full, domain names matches are partial.
 */
public class MatcherTest {
	@Test
	public void hostnamesShouldMatchDirectly() {
		var sut = new HostDomainNameMatcher("x", Set.of("hostname.com"), Set.of("foo.com", "bar.com"));
		
		assertThat(sut.test("nomatch"))
			.isFalse();
		assertThat(sut.test("hostname.com"))
			.isTrue();
	}


	@Test
	public void domainNamesShouldMatchDirectlyAndAsPrefixes() {
		var sut = new HostDomainNameMatcher("x", Set.of("hostname.com"), Set.of("foo.com", "bar.com"));
		
		assertThat(sut.test("com"))
			.isFalse();
		assertThat(sut.test("foo.com"))
			.isTrue();
		assertThat(sut.test("anything.foo.com"))
			.isTrue();
		assertThat(sut.test("long.anything.foo.com"))
			.isTrue();
	}

}
