package dk.mada.dns.wire.model;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DnsRecords {

	public static DnsRecordA aRecordFrom(DnsName name, InetAddress address, long ttl) {
		return new DnsRecordA(name, address, ttl);
	}

	public static DnsRecordA aRecordBlindFrom(DnsName name, long ttl) {
		try {
			return new DnsRecordA(name, InetAddress.getByAddress(new byte[] { 0, 0, 0, 0}), ttl);
		} catch (UnknownHostException e) {
			throw new UncheckedIOException(e);
		}
	}


	public static DnsRecordC cRecordFrom(DnsName name, DnsName aliasName, long ttl) {
		return new DnsRecordC(name, aliasName, ttl);
	}
	
	public static DnsRecordQ qRecordFrom(DnsName name) {
		return new DnsRecordQ(name);
	}


}
