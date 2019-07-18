package system.utility;

public interface Keywords {

	/* Traffic generator default values */
	public static final int InterArrivalTimeStartingSeed = 1000;
	public static final int FlowSizeStartingSeed = 2000;
	public static final int AccessLinkPropagationDelayStartingSeed = 3000;
	/* Scenario topologies */
	public static final int Dumbbell = 0;
	public static final int DataCenter = 1;
	public static final int ParkingLot = 2;

	/* RVG distributions */
	public static final int Exponential = 0;
	public static final int Uniform = 1;

	/* Event names */
	// Arrivals
	public static final String ArrivalToController = "Arrival to Controller";
	public static final String ArrivalToSwitch = "Arrival to Switch";
	public static final String ArrivalToHost = "Arrival to Host";
	// Departures
	public static final String DepartureFromController = "Departure From Controller";
	public static final String DepartureFromSwitch = "Departure from Switch";
	public static final String DepartureFromHost = "Departure from Host";

	/* Special addresses */
	public static final int ControllerID = -1;
	public static final int BroadcastDestination = -2;

	/* Special IDs */
	public static final int ControllerFLowID = -1;
	public static final int ReverseLinkIDOffSet = 10000;
	public static final int ACKStreamIDOffSet = 10000;

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

	/* Packet types */
	public static final int Segment = 0;
	public static final int SDNControl = 1;

	/* SDN control message types */
	public static final int BufferTokenUpdate = 0;
	public static final int FlowSetUp = 1;
	public static final int FlowRemoval = 2;

	/* Segment types */
	public static final int SYN = 0;
	public static final int SYNACK = 1;
	public static final int ACK = 2;
	public static final int DATA = 3;
	public static final int FIN = 4;
	public static final int FINACK = 5;
	public static final int CTRL = 6;
	public static final int UncontrolledFIN = 7;

	/* TCP congestion control states */
	public static final int CtrlMessageSize = 40 * 80;
	public static final int DataSegSize = 1000 * 8;
	public static final int ACKSegSize = 40 * 8;
	public static final int SYNSegSize = 40 * 8;
	public static final int FINSegSize = 40 * 8;
	public static final int CTRLSegSize = 40 * 8;
	public static final int SYNSeqNum = 0;
	public static final int SYNACKSeqNum = 1;
	public static final int CTRLSeqNum = -1;

	/*------------  Constant Values for different types of delays -----------*/
	public static final float CONTROLLER_RTT_DELAY = 1;
	public static final float CONTROLLER_PROCESS_DELAY = 1;
	public static final float NODE_PROCESS_DELAY = 1;
	public static final float HostProcessDelay = 0;
	/*-----------------------------------------------------------------------*/

	/* Chart Titles */
	public static final String ArrtivalToBottleneckLink = "Arrival of segments to the bottleneck link";
	public static final String Time = "Time(ms)";
	public static final String FlowID = "Flow ID";

	public static final String SeqNumPlot = "Transport Protocol Sequence Number Plot";
	public static final String SeqNumPlotXAxisTitle = "Time(ms)";
	public static final String SeqNumPlotYAxisTitle = "Segment Sequence Number";

	/* Network Settings */
	public static final int WAN = 0;
	public static final int LAN = 1;

	/* Traffic Settings */
	public static final int GeneralTraffic = 0;
	public static final int DatacenterTraffic = 1;
	public static final int MultilediaTraffic = 2;

	/* Scenarios */
	public static final String SenderAccessSwitchPrefix = "SASwitch_";
	public static final String ReceiverAccessSwitchPrefix = "RASwitch_";
	public static final String NetworkSwitchPrefix = "NSwitch_";
	public static final String SenderHostPrefix = "S_";
	public static final String ReceiverHostPrefix = "R_";
	public static final String FlowPrefix = "Flow_";
	public static final String ControllerPrefix = "C_";
	public static final String SenderAccessLinkPrefix = "SALink_";
	public static final String ReceiverAccessLinkPrefix = "RALink_";
	public static final String NetworkLinkPrefix = "NLink_";

}
