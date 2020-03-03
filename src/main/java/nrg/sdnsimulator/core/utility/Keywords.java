package nrg.sdnsimulator.core.utility;

public interface Keywords {
	interface Entities {
		interface Agents {
			interface Types {
				short Default = 0;
				short SDTCP = 1;
				short TCP = 2;
				short v2 = 3;
			}

			interface TimerTypes {
				short TimeToCycleTimer = 0;
				short InitialDelayTimer = 1;
				short IntervalTimer = 2;
				short InterSegmentDelayTimer = 3;
			}
		}

		interface Nodes {
			interface Types {
				short Host = 0;
				short SDNSwitch = 1;
				short Controller = 2;
			}
		}

		interface Controllers {
			interface Types {
				short Default = 0;
				short Controller_1 = 1;
				short Controller_2 = 2;
			}
		}

		interface Hosts {
			interface Types {
				short Default = 0;
				short Host_1 = 1;
			}
		}

		interface Switches {
			interface Types {
				short Default = 0;
				short Switch_1 = 1;
			}
		}

		interface Links {
			interface Types {
				short Default = 0;
				short Link_1 = 1;
			}
		}

		interface Buffers {
			interface Types {
				short Default = 0;
				short Buffer_1 = 1;
			}

			interface Size {
				int Unlimited = Integer.MAX_VALUE;
				int Minimum = 1;
			}

			interface Policy {
				short FIFO = 0;
			}
		}

		interface Labels {
			interface Prefixes {
				String ControllerPrefix = "C_";
				String FlowPrefix = "Flow_";
				String ControlLinkPrefix = "CLink_";
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
			String FairnessIndex = "Fairness Index";
			String FlowRejectionRate = "Flow Rejection Rate (%)";
			String MaxBtlQueueLength = "Max Bottleneck Queue Length (Segments)";
			String AvgBtlQueueLength = "Avg Bottleneck Queue Length (Segments)";
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
			short CtrlMessageSize = 40 * 8;
			short CTRLSegSize = 40 * 8;
			short FINSegSize = 40 * 8;
			short SYNSegSize = 40 * 8;
			short DataSegSize = 1000 * 8;

		}

		interface SpecialSequenceNumbers {
			short CTRLSeqNum = -1;
			short SYNSeqNum = 0;
		}

		interface Types {
			short ACK = 2;
			short NACK = 8;
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
			short Custom = 3;
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

	short ControllerNodeIDOffset = 10000;
	short SwitchNodeIDOffset = 20000;
	short HostNodeIDOffset = 30000;

	short BroadcastDestination = -2;

	/* Special IDs */
	short ControllerFLowID = -1;

	/* Special addresses */
	short ControllerID = -1;

	short ReverseLinkIDOffSet = 10000;

}
