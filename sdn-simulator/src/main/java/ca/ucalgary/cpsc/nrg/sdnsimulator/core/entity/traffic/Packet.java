package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.CtrlMessage;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.system.Main;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;

public class Packet {
	public CtrlMessage controlMessage;
	public Segment segment;
	private int size;
	public int type;

	public Packet(Segment segment, CtrlMessage controlMessage) {
		if (segment != null) {
			size = segment.getSize();
			type = Keywords.Packets.Types.Segment;
			this.segment = segment;
		} else if (controlMessage != null) {
			size = controlMessage.getSize();
			type = Keywords.Packets.Types.SDNControl;
			this.controlMessage = controlMessage;
		} else {
			Main.print("Error in packet type");
		}
	}

	public int getSize() {
		return size;
	}

}
