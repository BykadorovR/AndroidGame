package client;

import java.io.IOException;

public interface PlayerCoder {
	byte[] toWire(Player msg) throws IOException;

	Player fromWire(byte[] input) throws IOException;
}
