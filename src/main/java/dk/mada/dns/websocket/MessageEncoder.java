package dk.mada.dns.websocket;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.websocket.dto.DnsQueryEventDto;

// See http://json-b.net/docs/user-guide.html

public class MessageEncoder implements Encoder.Text<DnsQueryEventDto> {
	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);
	private Jsonb jsonb;

	public MessageEncoder() {
		var config = new JsonbConfig().withFormatting(false);
		jsonb = JsonbBuilder.create(config);
	}
	
	@Override
	public String encode(DnsQueryEventDto object) throws EncodeException {
		String str = jsonb.toJson(object);
		logger.trace("Try to encode {} -> {}", object, str);
		return str;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub

	}
}
