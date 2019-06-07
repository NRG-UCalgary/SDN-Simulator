package entities;

import java.util.ArrayList;
import java.util.HashMap;

public class CtrlMessage extends Entity {

	public int switchID;
	public int bufferMode;
	public HashMap<Integer, ArrayList<BufferToken>> tokens; // <HostID, tokensForEachBuffer>

	public CtrlMessage() {
		super(-1);
	}

}
