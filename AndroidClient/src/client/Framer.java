package client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Framer {
	//��������� �������������� ���������� (� ������ ���������� ����� � ������) � ���������� � �������� �����
	void frameMsg(byte[] message, OutputStream out) throws IOException;
	public void frameMsgList(ArrayList<byte[]> message, OutputStream out) throws IOException;
	public ArrayList<byte[]> getCountOfMessages() throws IOException;
	//����������� ����� �� ��������� � ������������ ���
	byte[] nextMsg() throws IOException;
}
