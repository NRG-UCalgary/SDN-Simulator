package entities;

import java.util.ArrayList;
import java.util.HashMap;

import entities.buffers.BufferToken;
import system.utility.*;

public class CtrlMessage {
	private int size = Keywords.CtrlMessageSize;
	public int switchID;
	public HashMap<Integer, ArrayList<BufferToken>> tokens; // <HostID, tokensForEachBuffer>

	public CtrlMessage() {
	}

	public int getSize() {
		return this.size;
	}

}
