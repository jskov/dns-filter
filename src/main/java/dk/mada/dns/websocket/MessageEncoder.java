package dk.mada.dns.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.rest.dto.EventDto;

// See http://json-b.net/docs/user-guide.html

public class MessageEncoder implements Encoder.Text<EventDto> {
	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

	@Override
	public String encode(EventDto object) throws EncodeException {
		logger.info("Try to encode {}", object);
		return "nope";
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
