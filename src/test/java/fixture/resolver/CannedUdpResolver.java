package fixture.resolver;

import java.nio.ByteBuffer;
import java.util.Optional;

import dk.mada.dns.resolver.DefaultResolver;
import dk.mada.dns.resolver.UdpNameServer;

/**
 * Resolver replying based on a canned UDP reply.
 */
public class CannedUdpResolver extends DefaultResolver {
	private CannedNameServer nameserver;

	private CannedUdpResolver(CannedNameServer nameserver) {
		super(nameserver);
		
		this.nameserver = nameserver;
	}

	public boolean hasBeenCalled() {
		return nameserver.callCount != 0;
	}
	
	public CannedUdpResolver(ByteBuffer firstReply) {
		this(new CannedNameServer(firstReply));
	}
	
	static class CannedNameServer implements UdpNameServer {
		private ByteBuffer firstReply;
		int callCount = 0;
		
		CannedNameServer(ByteBuffer firstReply) {
			this.firstReply = firstReply;
		}
		
		@Override
		public Optional<ByteBuffer> lookup(String hostname, ByteBuffer query) {
			callCount++;
			if (callCount == 2) {
				throw new IllegalStateException("Should only be called once!");
			}
			return Optional.ofNullable(firstReply);
		}
		
	}
}
