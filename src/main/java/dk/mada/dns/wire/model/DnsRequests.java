package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

import dk.mada.dns.wire.model.conversion.WireToModelConverter;

/**
 * DnsRequest factory methods.
 */
public class DnsRequests {
	public static DnsRequest fromWireData(byte[] data) {
		return fromWireData(ByteBuffer.wrap(data));
	}

	public static DnsRequest fromWireData(ByteBuffer data) {
		return WireToModelConverter.requestToModel(data);
	}

	public static DnsRequest fromWireRequest(DnsHeaderQuery header, DnsSection question, DnsSection additional, ByteBuffer wireRequest) {
		return new DnsRequest(header, question, additional, wireRequest);
	}
}
