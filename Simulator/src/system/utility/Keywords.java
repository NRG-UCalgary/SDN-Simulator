package system.utility;

public interface Keywords {

	interface Operations {
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
		int ControllerID = -1;
		int BroadcastDestination = -2;

		/* Special IDs */
		int ControllerFLowID = -1;
		int ReverseLinkIDOffSet = 10000;
		int ACKStreamIDOffSet = 10000;

		interface Nodes {
			interface Names {
				String Host = "Host";
				String Switch = "Switch";
			}
		}

		interface RoutingAlgorithms {
			int Dijkstra = 0;
			int Routing1 = 1;
		}

		interface Buffers {
			interface Policy {
				int FIFO = 0;
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
				int Segment = 0;
				int SDNControl = 1;
			}
		}

		interface SDNMessages {
			interface Types {
				int BufferTokenUpdate = 0;
				int FlowSetUp = 1;
				int FlowRemoval = 2;
			}

			int Size = 40 * 8;
		}

		interface Segments {
			interface Types {
				int SYN = 0;
				int SYNACK = 1;
				int ACK = 2;
				int DATA = 3;
				int FIN = 4;
				int FINACK = 5;
				int CTRL = 6;
				int UncontrolledFIN = 7;
			}

			interface Sizes {
				int CtrlMessageSize = 40 * 80;
				int DataSegSize = 1000 * 8;
				int ACKSegSize = 40 * 8;
				int SYNSegSize = 40 * 8;
				int FINSegSize = 40 * 8;
				int CTRLSegSize = 40 * 8;

			}

			interface SpecialSequenceNumbers {
				int SYNSeqNum = 0;
				int SYNACKSeqNum = 1;
				int CTRLSeqNum = -1;
			}
		}

	}

	interface Inputs {
		interface RandomVariableGenerator {
			interface Distributions {
				int Exponential = 0;
				int Uniform = 1;
				int Guassian = 2;
			}

			interface StartingSeeds {
				int InterArrivalTimeStartingSeed = 1000;
				int FlowSizeStartingSeed = 2000;
				int AccessLinkPropagationDelayStartingSeed = 3000;
				int NumberOfFLowsStartingSeed = 4000;
			}
		}

		interface Testbeds {
			interface Topologies {
				int Dumbbell = 0;
				int DataCenter = 1;
				int ParkingLot = 2;
			}

			interface Types {
				int WAN = 0;
				int LAN = 1;
				int DataCenter = 2;
			}
		}

		interface Traffics {
			interface Types {
				int GeneralTraffic = 0;
				int DatacenterTraffic = 1;
				int MultilediaTraffic = 2;
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
	}

	interface Outputs {
		interface Charts {
			interface Titles {
				String SeqNumPlot = "Transport Protocol Sequence Number Plot";
				String SeqNumPlotXAxisTitle = "Time(ms)";
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
					String Time = "Time(ms)";
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
	}

}
