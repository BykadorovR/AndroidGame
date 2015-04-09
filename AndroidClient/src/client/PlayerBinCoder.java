package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerBinCoder implements PlayerCoder {

	// manifest constants for encoding
	public static final int MAGIC = 0x54; // magic: 0101 01 00
	public static final int MAGIC_MASK = 0xfc; // Чтобы вычленить magic sequnce
												// from magic int :111111 00
	public static final int EXIT_FLAG = 0x02; // 0000 0010

	// Конвертирует vote message (объект) в последовательность байт в
	// соответствии с заданным протоколом
	public byte[] toWire(Player msg) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteStream); // converts
																	// ints
		byte magicAndFlags = MAGIC;
		if (msg.getExit() == true) {
			magicAndFlags |= EXIT_FLAG;
		}
		
		out.writeByte(magicAndFlags);
		out.writeBoolean(msg.getMove());
		out.writeUTF(msg.getID());
		out.writeDouble(msg.getCoordX());
		out.writeDouble(msg.getCoordY());
		out.writeDouble(msg.getCoordFireballX());
		out.writeDouble(msg.getCoordFireballY());
		out.flush();

		byte[] data = byteStream.toByteArray();
		return data;
	}

	// Парсит данный массив байт в соответствии с заданным протоколом и
	// конструирует объект
	public Player fromWire(byte[] input) throws IOException {
		ByteArrayInputStream bs = new ByteArrayInputStream(input);
		DataInputStream in = new DataInputStream(bs);
		byte magic = in.readByte();

		boolean exit;
		if ((magic & MAGIC_MASK) != MAGIC) {

			throw new IOException("Bad Magic #: " + (magic & MAGIC_MASK));
		} else {
			exit = ((magic & EXIT_FLAG) != 0);
		}
		boolean move = in.readBoolean();
		String ID = in.readUTF();
		double coordX = in.readDouble();
		double coordY = in.readDouble();
		double fireballX = in.readDouble();
		double fireballY = in.readDouble();
		
		Player newMsg = new Player(coordX, coordY);
		newMsg.setCoordFireballX(fireballX);
		newMsg.setCoordFireballY(fireballY);	
		
		System.out.println(fireballX + " "+ fireballY);
		newMsg.setExit(exit);
		newMsg.setID(ID);
		newMsg.setMove(move);
		return newMsg;

	}

}
