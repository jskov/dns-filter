package dk.mada.dns.resolver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.service.DevelopmentDebugging;

/**
 * External gateway to upstream DNS service.
 * 
 * Very heroic code to work around non-working timeouts. So instead,
 * keep a queue of outstanding requests and terminate them if they
 * take too long.
 */
@ApplicationScoped
public class ExternalDnsGateway {
	private static final Logger logger = LoggerFactory.getLogger(ExternalDnsGateway.class);
	private static final int UPSTREAM_TIMEOUT_MS = 2_000;

	private static final InetSocketAddress target = UpsteamDnsServer.getActive();

	private final Set<WaitingInstance> queue = Collections.synchronizedSet(new HashSet<>());
	private final Semaphore actionSemaphore = new Semaphore(0);

	private final ExecutorService rogueService = Executors.newFixedThreadPool(1);

	private DevelopmentDebugging devDebugging;
	
	@Inject
	public ExternalDnsGateway(DevelopmentDebugging devDebugging) {
		this.devDebugging = devDebugging;
	}
	
	
	// Run in a rogue thread to avoid container reaping it
	public void startBackgroundReaper() {
		rogueService.submit(this::startReaper);
	}
	
	private void startReaper() {
		int i = 0;
		logger.info("Reaper started in {}", Thread.currentThread());
		while(true) {
			
			try {
				logger.debug("Reaper waiting for action...");
				boolean gotIt = actionSemaphore.tryAcquire(10, TimeUnit.MINUTES);
				if (!gotIt) {
					logger.debug("Hunting without semaphore");
				}
				
				logger.debug("Reaper hunting...");
				while (!queue.isEmpty()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						logger.warn("Reaper interrupted while waiting", e);
					}
					
					if (i++ % 200 == 0) {
						logger.debug("Reaper breathing...");
					}
					
					
					long now = System.currentTimeMillis();
					synchronized (queue) {
						for (WaitingInstance wi : queue) {
							if (now >= wi.killAtMs) {
								wi.thread.interrupt();
								logger.debug("Interrupted {}", wi.thread.getName());
							}
						}
					}
				}
				
			} catch (InterruptedException e1) {
				logger.warn("IGNORED reaper interrupt!");
			}
		}
	}
	
	public Optional<ByteBuffer> passOnToUpstreamServer(String query, ByteBuffer bb) {
		for (int i = 1; i <= 3; i++) {
			logger.debug("Try resolving {}", query);
			try {
				return Optional.of(passOnToUpstreamServerWithTimeout(query, bb));
			} catch (ClosedByInterruptException e) {
				logger.warn("Timeout on #{} request of {}", i, query);
			}
		}
		logger.warn("Gave up resolving {}", query);
		return Optional.empty();
	}

	static class WaitingInstance {
		final Thread thread;
		final long killAtMs;

		public WaitingInstance(Thread thread, long killAtMs) {
			super();
			this.thread = thread;
			this.killAtMs = killAtMs;
		}
	}

//	Hexer.printForDevelopment("Reply", reply, Collections.emptySet());
//	reply.rewind();

	
	private ByteBuffer passOnToUpstreamServerWithTimeout(String query, ByteBuffer bb) throws ClosedByInterruptException {
		devDebugging.devOutputWireData(query, "Request for " + query, bb);
		
		long start = System.currentTimeMillis();
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.connect(target);
			
			bb.rewind();
			channel.send(bb, target);
	
			ByteBuffer reply = ByteBuffer.allocate(512);
	
			WaitingInstance ticket = new WaitingInstance(Thread.currentThread(), start + UPSTREAM_TIMEOUT_MS);
			try {
				queue.add(ticket);
				actionSemaphore.release(1);
				
				channel.read(reply);
			} finally {
				queue.remove(ticket);
			}
			reply.flip();
	
			devDebugging.devOutputWireData(query, "Reply for " + query, reply);
			reply.rewind();
			
			devDebugging.stopOutputForHost(query);
			
			long time = System.currentTimeMillis() - start;
			logger.debug("Upstream reply in {}ms", time);
			
			return reply;
		} catch (ClosedByInterruptException e) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e1) {
				logger.debug("Cleared interrupted state");
			}
			throw e;
		} catch (IOException e) {
			throw new IllegalStateException("Failed to query upstream DNS server", e);
		}
	}
}
