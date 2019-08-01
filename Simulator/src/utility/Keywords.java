package utility;

public interface Keywords {

	interface Agents {
		interface Types {
			String RBTCP = "RBTCP";
			String SDTCP = "SDTCP";
			String TCP = "TCP";
		}
	}

	interface Buffers {
		interface Policy {
			short FIFO = 0;
		}
	}
	interface Charts {
		interface MainFactors {
			interface Titles {
				String AvgFlowInterArrivalTime = "Avg Flow InterArrival Time (us)";
				String FlowSizeFactor = "Flow Size (# Segments)";
				String NumberOfFlowsFactor = "Number Of Flows ";
			}

		}

		interface Metrics {
			interface Titles {
				String ArrtivalToBottleneckLink = "Arrival of segments to the bottleneck link";
				String FlowID = "Flow ID";
				String Time = "Time(us)";
			}
		}

		interface SecondFactors {
			interface Titles {
			}
		}

		interface Titles {
			String SeqNumPlot = "Transport Protocol Sequence Number Plot";
			String SeqNumPlotXAxisTitle = "Time(us)";
			String SeqNumPlotYAxisTitle = "Segment Sequence Number";
		}
	}

	interface DefaultTestValues {
		interface FlowInterArrivalTime {
			short Distribution = RandomVariableGenerator.Distributions.Exponential;
			double Mean = 100000;
			double STD = 10000;
		}

		interface FlowSize {
			short Distribution = RandomVariableGenerator.Distributions.LogNormal;
			double Mean = 10000;
			double STD = 1000;
		}

		interface NumberOfFlows {
			short Distribution = RandomVariableGenerator.Distributions.Constant;
			double Mean = 10;
			double STD = 0;
		}

		float FirstFlowArrival = 0;
	}
	interface Entities {
		interface Labels {
			interface Prefixes {
				String ControllerPrefix = "C_";
				String FlowPrefix = "Flow_";
				String NetworkLinkPrefix = "NLink_";
				String NetworkSwitchPrefix = "NSwitch_";
				String ReceiverAccessLinkPrefix = "RALink_";
				String ReceiverAccessSwitchPrefix = "RASwitch_";
				String ReceiverHostPrefix = "R_";
				String SenderAccessLinkPrefix = "SALink_";
				String SenderAccessSwitchPrefix = "SASwitch_";
				String SenderHostPrefix = "S_";
			}
		}
	}
	interface Events {
		interface Names {
			interface Arrivals {
				String ArrivalToController = "Arrival to Controller";
				String ArrivalToHost = "Arrival to Host";
				String ArrivalToSwitch = "Arrival to Switch";
			}

			interface Departures {
				String DepartureFromController = "Departure From Controller";
				String DepartureFromHost = "Departure from Host";
				String DepartureFromSwitch = "Departure from Switch";
			}
		}
	}

	interface Metrics {
		interface Names {
			String AvgFlowCompletionTime = "Avg Flow Completion Time (us)";
			String AvgFlowStartupDelay = "Avg Flow Setup Delay (us)";
			String AvgFlowThroughput = "Avg Flow Throughput";
			String BtlUtilization = "Bottleneck Link Utilization";
			String FlowRejectionRate = "Flow Rejection Rate (%)";
			String MaxBtlBufferOccupancy = "Max Bottlenek Buffer Occupancy (Packets)";
			String VarBtlUtilizationShareOverFlowSize = "Var (Flow Btl Util Share / Flow Size)";
			String VarFlowCompletionTimeOverFlowSize = "Var (Flow Completion Time / Flow Size)";

		}
	}

	interface Nodes {
		interface Names {
			String Host = "Host";
			String Switch = "Switch";
		}
	}

	interface Packets {
		interface Types {
			short SDNControl = 1;
			short Segment = 0;
		}
	}

	interface RandomVariableGenerator {
		interface Distributions {
			short Constant = 0;
			short Exponential = 2;
			short Gamma = 5;
			short Guassian = 3;
			short LogNormal = 4;
			short Uniform = 1;

		}

		interface StartingSeeds {
			short AccessLinkPropagationDelayStartingSeed = 3000;
			short FlowSizeStartingSeed = 2000;
			short InterArrivalTimeStartingSeed = 1000;
			short NumberOfFLowsStartingSeed = 4000;
		}
	}

	interface RoutingAlgorithms {
		short Dijkstra = 0;
		short Routing1 = 1;
	}

	interface Scenarios {
		interface Names {
			String FlowSizeStudy = "Flow Size Study";
			String NumberOfFlowsStudy = "Number Of Flows Study";
		}
	}

	interface SDNMessages {
		interface Types {
			short BufferTokenUpdate = 0;
			short FlowRemoval = 2;
			short FlowSetUp = 1;
		}

		short Size = 40 * 8;
	}

	interface Segments {
		interface Sizes {
			short ACKSegSize = 40 * 8;
			short CtrlMessageSize = 40 * 80;
			short CTRLSegSize = 40 * 8;
			short DataSegSize = 1000 * 8;
			short FINSegSize = 40 * 8;
			short SYNSegSize = 40 * 8;

		}

		interface SpecialSequenceNumbers {
			short CTRLSeqNum = -1;
			short SYNACKSeqNum = 1;
			short SYNSeqNum = 0;
		}

		interface Types {
			short ACK = 2;
			short CTRL = 6;
			short DATA = 3;
			short FIN = 4;
			short FINACK = 5;
			short SYN = 0;
			short SYNACK = 1;
			short UncontrolledFIN = 7;
		}
	}

	interface Testbeds {
		interface Topologies {
			short DataCenter = 1;
			short Dumbbell = 0;
			short ParkingLot = 2;
		}

		interface Types {
			short DataCenter = 2;
			short LAN = 1;
			short WAN = 0;
		}
	}

	interface Traffics {
		interface Types {
			short DatacenterTraffic = 1;
			short GeneralTraffic = 0;
			short MultilediaTraffic = 2;
		}
	}

	short ACKStreamIDOffSet = 10000;

	short BroadcastDestination = -2;

	/* Special IDs */
	short ControllerFLowID = -1;

	/* Special addresses */
	short ControllerID = -1;

	short ReverseLinkIDOffSet = 10000;

}
