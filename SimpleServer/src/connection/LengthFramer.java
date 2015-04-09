package connection;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class LengthFramer implements Framer {
	
	public static final int BYTEMASK = 0xff; //1111 1111
	public static final int SHORTMASK = 0xffff; //1111 1111 1111 1111
	public static final int BYTESHIFT = 8; //
	ArrayList<byte[]> msg;
	private DataInputStream in; // wrapper for data I/O

	public LengthFramer(InputStream in) throws IOException {
		this.in = new DataInputStream(in);
		msg = new ArrayList<byte[]>();
	}

	public void frameMsg(byte[] message, OutputStream out) throws IOException {
		
		out.write(message.length);
		// write message
		out.write(message);
		out.flush();
	}

	//Чтобы передать список "игроков"
	public void frameMsgList(ArrayList<byte[]> message, OutputStream out) throws IOException {
		out.write(message.size());
		for (int i=0; i<message.size(); i++) {
			out.write(message.get(i).length);
			out.write(message.get(i));
		}
		out.flush();
	}
	
	public ArrayList<byte[]> getCountOfMessages () throws IOException {
		msg.clear();
		int length=0;
		int size = 0;
		try {
			size = in.read();
			for (int i=0; i<size; i++) {
				length = in.read();
				msg.add(new byte[length]);
				in.readFully(msg.get(i), 0, length);
				
			}
		} catch (EOFException e) {
			System.out.println("SOM");
			e.printStackTrace();
			return null;
		}
		
		return msg;
	}
	
	public byte[] nextMsg() throws IOException {
		int length;
		try { 
			length = in.read(); // read 2 bytes
		} catch (EOFException e) { // no (or 1 byte) message
			e.printStackTrace();
			System.out.println("KOM");
			return null;
		}
		// 0 <= length <= 2*10^9
		byte[] msg = new byte[length];
		in.readFully(msg); // if exception, it's a framing error.
		return msg;
	}


	
}
