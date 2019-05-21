package system;

public interface Keywords {

	/* Special addresses */
	public static final int ControllerID = -1;
	public static final int BroadcastDestination = -2;

	/* Special IDs */
	public static final int ControllerFLowID = -1;

	/* Event types */
	public static final int ArrivalToSwitch = 0;
	public static final int ArrivalToHost = 1;
	public static final int DepartureFromHost = 2;
	public static final int DepartureFromSwitch = 3;
	public static final int TCPTimeOut = 4;

	/* Node types */
	public static final int Host = 0;
	public static final int Switch = 1;
	public static final int AccessSwitch = 2;
	public static final int NetworkSwitch = 3;

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

	public static final int DataSegSize = 1000;
	public static final int ACKSegSize = 40;
	public static final int SYNSegSize = 40;
	public static final int FINSegSize = 40;
	public static final int CTRLSegSize = 40;
	public static final int SlowStartSSThreshFactor = 64;
	public static final int FastRecoveryCWNDDivindingFactore = 2;
	public static final int TimeOutSlowStartCWNDDivindingFactore = 2;
	public static final int SYNSeqNum = 0;

	/*------------  Constant Values for different types of delays -----------*/
	public static final double CONTROLLER_RTT_DELAY = 1.0;
	public static final double CONTROLLER_PROCESS_DELAY = 1.0;
	public static final double NODE_PROCESS_DELAY = 1.0;
	/*-----------------------------------------------------------------------*/

}
