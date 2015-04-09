package client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Framer {
	//ƒобавл€ет дополнительную информацию (в данной реализации длину в начало) и записывает в выходный стрим
	void frameMsg(byte[] message, OutputStream out) throws IOException;
	public void frameMsgList(ArrayList<byte[]> message, OutputStream out) throws IOException;
	public ArrayList<byte[]> getCountOfMessages() throws IOException;
	//¬ытаскивает длину из сообщени€ и обрабатывает его
	byte[] nextMsg() throws IOException;
}
