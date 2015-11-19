package model;

import java.io.IOException;

public interface ICoderModel {
	byte[] toWire(Player msg) throws IOException;
	Player fromWire(byte[] input) throws IOException;
}
