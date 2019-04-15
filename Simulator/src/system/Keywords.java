package system;

public class Keywords {

	/* Event types */
	public static final int ArrivalEvent = 0;
	public static final int DepartureEvent = 1;
	public static final int TCPTimeOutEvent = 2;

	/* Buffer */
	// modes
	public final static int NORMALBUFFER = 0;
	public final static int TOKENBASEDBUFFER = 1;
	// policies
	public final static int FIFO = 0;

	/* TCP Agent */

	// Segment Types
	public static final int SYN = 0;
	public static final int SYNACK = 1;
	public static final int ACK = 2;
	public static final int DATA = 3;
	public static final int FIN = 4;
	public static final int FINACK = 5;
	public static final int CTRL = 6;

	public static final String ACKFlowExtention = ".ACK";

	// TCP congestion control states
	public static final int SlowStart = 0;
	public static final int CongAvoidance = 1;
	public static final int FastRecovery = 2;

	public static final double ACKGenerationTime = 0.5; // usually is less than 500MS

	public static final int DataSegSize = 1000;
	public static final int ACKSegSize = 40;
	public static final int SYNSegSize = 40;
	public static final int FINSegSize = 40;
	public static final int SlowStartSSThreshFactor = 64;
	public static final int FastRecoveryCWNDDivindingFactore = 2;
	public static final int TimeOutSlowStartCWNDDivindingFactore = 2;
	public static final int SYNSeqNum = 0;

	public Keywords() {
		// TODO Auto-generated constructor stub
	}

}
