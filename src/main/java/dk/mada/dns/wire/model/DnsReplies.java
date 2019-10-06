package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

import dk.mada.dns.wire.model.conversion.ModelToWireConverter;
import dk.mada.dns.wire.model.conversion.WireToModelConverter;

public class DnsReplies {
	public static DnsReply fromAnswer(DnsHeader header, DnsSection question, DnsSection answer) {
		var res = new DnsReply(header, question);
		res.setAnswer(answer);
		return res;
	}
	
	public static DnsReply fromWireData(ByteBuffer data) {
		return WireToModelConverter.replyToModel(data);
	}

	public static ByteBuffer toWireFormat(DnsReply reply) {
		return ModelToWireConverter.modelToWire(reply);
	}
}
