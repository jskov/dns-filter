package dk.mada.dns.util;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hexer {
	private static final Logger logger = LoggerFactory.getLogger(Hexer.class);

	private Hexer() {}
	
	public static String toHexLine(ByteBuffer bb) {
		int pos = bb.position();
		bb.rewind();
		StringBuilder sb = new StringBuilder();
		while (bb.hasRemaining()) {
			byte b = bb.get();
			if (Byte.toUnsignedInt(b) >= 128) {
				sb.append("(byte)");
			}
			sb.append(String.format("0x%02x, ", b));
		}
		bb.position(pos);
		return sb.toString();
	}

	public static String toHexBlock(Optional<ByteBuffer> bb) {
		return bb.map(Hexer::toHexBlock)
				.orElse("<No data>");
	}
	
	public static String toHexBlock(ByteBuffer bb) {
		int pos = bb.position();
		bb.rewind();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		StringBuilder ascii = new StringBuilder();
		while (bb.hasRemaining()) {
			if (i % 16 == 0) {
				sb.append(String.format("0x%04x", i));
			}
			if (i % 8 == 0) {
				sb.append(' ');
			}
			byte b = bb.get();
			i++;
			sb.append(String.format("%02x ", b));
			int bint = Byte.toUnsignedInt(b);
			ascii.append(bint >= 32 && bint < 127 ? (char)b : ".");
			if (i % 16 == 0) {
				sb.append(ascii.toString());
				ascii = new StringBuilder();
				sb.append("\n");
			}
		}
		
		if (i % 16 != 0) {
			while (i % 16 != 0) {
				sb.append("   ");
				i++;
			}
			sb.append(ascii);
		}

		bb.position(pos);
		return sb.toString();
	}
	
	public static String hexPosition(ByteBuffer bb) {
		return hexShort(bb.position());
	}

	public static String hexShort(int offset) {
		return String.format("0x%04x", offset);
	}
	
	public static void printForDevelopment(String title, ByteBuffer bb) {
		logger.warn("{}", title);
		logger.warn("CODE: byte[] req = new byte[] {{}};", toHexLine(bb));
		logger.warn("BLOCK:\n{}", toHexBlock(bb));
	}
}
