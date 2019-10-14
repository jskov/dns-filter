package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;
import java.util.List;

import dk.mada.dns.wire.model.conversion.ModelToWireConverter;
import dk.mada.dns.wire.model.conversion.WireToModelConverter;

public class DnsReplies {
	public static DnsReply fromAnswer(DnsHeaderReply header, DnsSection question, DnsSection answer) {
		var res = new DnsReply(header, question);
		res.setAnswer(answer);
		return res;
	}
	
	public static DnsReply fromRequestWithAnswer(DnsRequest request, DnsRecord answer) {
		var qheader = request.getHeader();
		var header = DnsHeaderReplies.fromRequest(qheader, (short)1, (short)0, (short)0);
		// FIXME: copy AR
		
		return DnsReplies.fromAnswer(header, request.getQuestionSection(), DnsSections.from(DnsSectionType.ANSWER, List.of(answer)));
	}

	public static DnsReply fromRequestWithAnswers(DnsRequest request, DnsRecord... answers) {
		var qheader = request.getHeader();
		var header = DnsHeaderReplies.fromRequest(qheader, (short)1, (short)0, (short)0);
		// FIXME: copy AR
		
		return DnsReplies.fromAnswer(header, request.getQuestionSection(), DnsSections.ofAnswers(answers));
	}

	public static DnsReply fromWireData(ByteBuffer data) {
		return WireToModelConverter.replyToModel(data);
	}

	public static ByteBuffer toWireFormat(DnsReply reply) {
		return ModelToWireConverter.modelToWire(reply);
	}
}
