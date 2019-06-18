package entities;

import java.util.ArrayList;
import java.util.HashMap;

import entities.buffers.BufferToken;

public class CtrlMessage extends Entity {

	public int switchID;
	//public int bufferMode;
	public HashMap<Integer, ArrayList<BufferToken>> tokens; // <HostID, tokensForEachBuffer>

	public CtrlMessage() {
		super(-1);
	}

}
