package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

import dk.mada.dns.wire.model.conversion.ModelToWireConverter;
import dk.mada.dns.wire.model.conversion.WireToModelConverter;

public class DnsReplies {
	public static DnsReply fromAnswer(DnsHeaderReply header, DnsSectionQuestion question, DnsSectionAnswer answer, DnsSectionAdditional additional, ByteBuffer optWireData) {
		var res = new DnsReply(header, question);
		res.setAnswer(answer);
		res.setAdditional(additional);
		res.setOptWireReply(optWireData);
		return res;
	}
	
	public static DnsReply fromRequestToBlockedReply(DnsRequest request, DnsRecord answer) {
		var qheader = request.getHeader();
		var header = DnsHeaderReplies.fromRequest(qheader, (short)1, (short)0, (short)0);
		
		DnsSectionAnswer answerSection = DnsSections.ofAnswers(answer);
		DnsSectionAdditional additionalSection = DnsSections.emptyAdditionals();
		return DnsReplies.fromAnswer(header, request.getQuestionSection(), answerSection, additionalSection, null);
	}

	public static DnsReply fromRequestWithAnswer(DnsRequest request, DnsSectionAnswer answerSection, DnsSectionAdditional additionalSection) {
		var qheader = request.getHeader();
		var header = DnsHeaderReplies.fromRequest(qheader, (short)answerSection.getRecords().size(), (short)0, (short)additionalSection.getSize());
		
		return DnsReplies.fromAnswer(header, request.getQuestionSection(), answerSection, additionalSection, null);
	}

	public static DnsReply fromRequestWithAnswers(DnsRequest request, DnsRecord... answers) {
		var qheader = request.getHeader();
		var header = DnsHeaderReplies.fromRequest(qheader, (short)answers.length, (short)0, (short)0);
		// FIXME: copy AR
		DnsSectionAdditional additionalSection = DnsSections.emptyAdditionals();
		
		return DnsReplies.fromAnswer(header, request.getQuestionSection(), DnsSections.ofAnswers(answers), additionalSection, null);
	}

	public static DnsReply fromWireData(byte[] data) {
		return WireToModelConverter.replyToModel(ByteBuffer.wrap(data));
	}
	
	public static DnsReply fromWireData(ByteBuffer data) {
		return WireToModelConverter.replyToModel(data);
	}

	public static ByteBuffer toWireFormat(DnsReply reply) {
		return ModelToWireConverter.modelToWire(reply);
	}
}
