package switches;

import entities.CtrlMessage;
import entities.Link;
import entities.SDNSwitch;
import system.Keywords;
import system.Network;

public class SDNSwitchv1 extends SDNSwitch {

	/* Constructor */
	public SDNSwitchv1(int ID, Link controlLink) {
		super(ID, controlLink);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from SDNSwitch) ----- */
	/* --------------------------------------------------- */
	public Network recvCtrlMessage(Network net, CtrlMessage message) {
		double releaseTime;
		int i = 0;
		for (int hostID : accessLinks.keySet()) {
			releaseTime = (net.getCurrentTime() + message.waitBeforeRelease)
					+ (message.interFlowDelay * i - accessLinks.get(hostID).getTotalDelay(Keywords.ACKSegSize));
			accessLinks.get(hostID).setBufferMode(message.bufferMode, releaseTime, message.ackNumber);
			i++;
		}

		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

}
