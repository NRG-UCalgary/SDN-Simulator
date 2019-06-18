package utilities;

public interface Keywords {

	/* Event names */
	public static final String ArrivalToController = "Arrival to Controller";
	public static final String ArrivalToSwitch = "Arrival to Switch";
	public static final String ArrivalToHost = "Arrival to Host";
	public static final String Departure = "Departure from Switch";
	public static final String FlowPathSetup = "Flow Path Setup on Switch";
	/* Special addresses */
	public static final int ControllerID = -1;
	public static final int BroadcastDestination = -2;

	/* Special IDs */
	public static final int ControllerFLowID = -1;

	/* Node types */
	public static final String Host = "Host";
	public static final String Switch = "Switch";

	/* Routing algorithms */
	public static final int Dijkstra = 0;
	public static final int Routing1 = 1;

	/* Buffer */
	// modes:
	public final static int FlushBuffer = 0;
	public final static int TokenBasedBuffer = 1;
	// policies:
	public final static int FIFO = 0;

	/* Agent types */
	public static final String TCP = "TCP";
	public static final String SDTCP = "SDTCP";
	public static final String RBTCP = "RBTCP";

	/* Segment types */
	public static final int SYN = 0;
	public static final int SYNACK = 1;
	public static final int ACK = 2;
	public static final int DATA = 3;
	public static final int FIN = 4;
	public static final int FINACK = 5;
	public static final int CTRL = 6;

	public static final String ACKFlowExtention = ".ACK";

	/* TCP congestion control states */
	public static final int SlowStart = 0;
	public static final int CongAvoidance = 1;
	public static final int FastRecovery = 2;

	public static final double ACKGenerationTime = 0.5; // usually is less than 500MS

	public static final int DataSegSize = 1000 * 8;
	public static final int ACKSegSize = 40 * 8;
	public static final int SYNSegSize = 40 * 8;
	public static final int FINSegSize = 40 * 8;
	public static final int CTRLSegSize = 40 * 8;
	public static final int SlowStartSSThreshFactor = 64;
	public static final int FastRecoveryCWNDDivindingFactore = 2;
	public static final int TimeOutSlowStartCWNDDivindingFactore = 2;
	public static final int SYNSeqNum = 0;
	public static final int SYNACKSeqNum = 1;
	public static final int CTRLSeqNum = -1;

	/*------------  Constant Values for different types of delays -----------*/
	public static final double CONTROLLER_RTT_DELAY = 1.0;
	public static final double CONTROLLER_PROCESS_DELAY = 1.0;
	public static final double NODE_PROCESS_DELAY = 1.0;
	/*-----------------------------------------------------------------------*/

}
