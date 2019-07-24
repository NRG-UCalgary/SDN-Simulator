package system.utility;

public interface Keywords {

	interface Events {
		interface Names {
			interface Arrivals {
				String ArrivalToController = "Arrival to Controller";
				String ArrivalToSwitch = "Arrival to Switch";
				String ArrivalToHost = "Arrival to Host";
			}

			interface Departures {
				String DepartureFromController = "Departure From Controller";
				String DepartureFromSwitch = "Departure from Switch";
				String DepartureFromHost = "Departure from Host";
			}
		}
	}

	/* Special addresses */
	short ControllerID = -1;
	short BroadcastDestination = -2;

	/* Special IDs */
	short ControllerFLowID = -1;
	short ReverseLinkIDOffSet = 10000;
	short ACKStreamIDOffSet = 10000;

	interface Nodes {
		interface Names {
			String Host = "Host";
			String Switch = "Switch";
		}
	}

	interface RoutingAlgorithms {
		short Dijkstra = 0;
		short Routing1 = 1;
	}

	interface Buffers {
		interface Policy {
			short FIFO = 0;
		}
	}

	interface Agents {
		interface Types {
			String TCP = "TCP";
			String SDTCP = "SDTCP";
			String RBTCP = "RBTCP";
		}
	}

	interface Packets {
		interface Types {
			short Segment = 0;
			short SDNControl = 1;
		}
	}

	interface SDNMessages {
		interface Types {
			short BufferTokenUpdate = 0;
			short FlowSetUp = 1;
			short FlowRemoval = 2;
		}

		short Size = 40 * 8;
	}

	interface Segments {
		interface Types {
			short SYN = 0;
			short SYNACK = 1;
			short ACK = 2;
			short DATA = 3;
			short FIN = 4;
			short FINACK = 5;
			short CTRL = 6;
			short UncontrolledFIN = 7;
		}

		interface Sizes {
			short CtrlMessageSize = 40 * 80;
			short DataSegSize = 1000 * 8;
			short ACKSegSize = 40 * 8;
			short SYNSegSize = 40 * 8;
			short FINSegSize = 40 * 8;
			short CTRLSegSize = 40 * 8;

		}

		interface SpecialSequenceNumbers {
			short SYNSeqNum = 0;
			short SYNACKSeqNum = 1;
			short CTRLSeqNum = -1;
		}
	}

	interface RandomVariableGenerator {
		interface Distributions {
			short Constant = 0;
			short Uniform = 1;
			short Exponential = 2;
			short Guassian = 3;
			short LogNormal = 4;
			short Gamma = 5;

		}

		interface StartingSeeds {
			short InterArrivalTimeStartingSeed = 1000;
			short FlowSizeStartingSeed = 2000;
			short AccessLinkPropagationDelayStartingSeed = 3000;
			short NumberOfFLowsStartingSeed = 4000;
		}
	}

	interface Testbeds {
		interface Topologies {
			short Dumbbell = 0;
			short DataCenter = 1;
			short ParkingLot = 2;
		}

		interface Types {
			short WAN = 0;
			short LAN = 1;
			short DataCenter = 2;
		}
	}

	interface Traffics {
		interface Types {
			short GeneralTraffic = 0;
			short DatacenterTraffic = 1;
			short MultilediaTraffic = 2;
		}
	}

	interface Entities {
		interface Labels {
			interface Prefixes {
				String SenderAccessSwitchPrefix = "SASwitch_";
				String ReceiverAccessSwitchPrefix = "RASwitch_";
				String NetworkSwitchPrefix = "NSwitch_";
				String SenderHostPrefix = "S_";
				String ReceiverHostPrefix = "R_";
				String FlowPrefix = "Flow_";
				String ControllerPrefix = "C_";
				String SenderAccessLinkPrefix = "SALink_";
				String ReceiverAccessLinkPrefix = "RALink_";
				String NetworkLinkPrefix = "NLink_";
			}
		}
	}

	interface Charts {
		interface Titles {
			String SeqNumPlot = "Transport Protocol Sequence Number Plot";
			String SeqNumPlotXAxisTitle = "Time(us)";
			String SeqNumPlotYAxisTitle = "Segment Sequence Number";
		}

		interface MainFactors {
			interface Titles {
				String NumberOfFlowsFactor = "Number Of Flows ";
				String FlowSizeFactor = "Flow Size (# Segments)";
				String AvgFlowInterArrivalTime = "Avg Flow InterArrival Time (us)";
			}

		}

		interface SecondFactors {
			interface Titles {
			}
		}

		interface Metrics {
			interface Titles {
				String ArrtivalToBottleneckLink = "Arrival of segments to the bottleneck link";
				String Time = "Time(us)";
				String FlowID = "Flow ID";
			}
		}
	}

	interface Scenarios {
		interface Names {
			String NumberOfFlowsStudy = "Number Of Flows Study";
			String FlowSizeStudy = "Flow Size Study";
		}
	}

	interface Metrics {
		interface Names {
			String AvgFlowCompletionTime = "Avg Flow Completion Time (us)";
			String AvgFlowStartupDelay = "Avg Flow Setup Delay (us)";
			String AvgFlowThroughput = "Avg Flow Throughput";
			String BtlUtilization = "Bottleneck Link Utilization";
			String MaxBtlBufferOccupancy = "Max Bottlenek Buffer Occupancy (Packets)";
			String FlowRejectionRate = "Flow Rejection Rate (%)";
			String VarFlowCompletionTimeOverFlowSize = "Var (Flow Completion Time / Flow Size)";
			String VarBtlUtilizationShareOverFlowSize = "Var (Flow Btl Util Share / Flow Size)";

		}
	}

	interface DefaultTestValues {
		float FirstFlowArrival = 0;

		interface NumberOfFlows {
			short Distribution = RandomVariableGenerator.Distributions.Constant;
			double Mean = 10;
			double STD = 0;
		}

		interface FlowSize {
			short Distribution = RandomVariableGenerator.Distributions.LogNormal;
			double Mean = 10000;
			double STD = 1000;
		}

		interface FlowInterArrivalTime {
			short Distribution = RandomVariableGenerator.Distributions.Exponential;
			double Mean = 100000;
			double STD = 10000;
		}
	}

}
