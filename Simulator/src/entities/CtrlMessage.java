package entities;

import java.util.HashMap;

import entities.buffers.BufferToken;
import system.utility.*;

public class CtrlMessage {
	private int size = Keywords.CtrlMessageSize;
	public int switchID;
	public HashMap<Integer, BufferToken> ccTokenOfHostID; // <HostID, tokenForEachBuffer>

	public CtrlMessage() {
	}

	public int getSize() {
		return this.size;
	}

}
