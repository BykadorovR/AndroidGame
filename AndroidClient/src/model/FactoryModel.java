package model;

import java.io.IOException;
import java.io.InputStream;


public class FactoryModel {
	public ICoderModel createBinCoderModel() {	
		return new BinCoderModel();
	}
	
	public IFramerModel createLengthFramerModel(InputStream in) throws IOException {
		return new LengthFramerModel(in);
	}
	
	public ICommunicationModel createMultiplayerCommunicationModel() {
		return new MultiplayerCommunicationModel();
	}
	
	
	public ICommunicationModel createSingleCommunicationModel() {
		return new SingleCommunicationModel();
	}
}
