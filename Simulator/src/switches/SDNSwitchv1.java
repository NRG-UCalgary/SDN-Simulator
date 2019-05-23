package switches;

import entities.CtrlMessage;
import entities.Link;
import entities.SDNSwitch;
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
		for (int hostID : message.tokens.keySet()) {
			accessLinks.get(hostID).buffer.updateTokenList(message.tokens.get(hostID));
		}
		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

}
