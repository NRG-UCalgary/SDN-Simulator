package simulator.entities.network.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import simulator.Network;
import simulator.entities.network.*;
import simulator.entities.traffic.*;
import simulator.events.TimeoutEvent;
import system.Main;
import utility.*;

public class Senderv2 extends Agent {

	private float RTT = 12.16f;
	private boolean hasRecvdSYNACK;
	private boolean hasSentFIN;

	private HashMap<Integer, Timer> timerOfTimerID;

	private ArrayList<Integer> timeToNextCycleTimerIDs;
	private int timerIndex;

	private HashMap<Integer, CCParams> nextCCParamsOfTimerID;
	private CCParams ccParams;

	private int remainingSegments;
	private int mostRecentSequenceNum;
	private int lastSentSeqNum;
	private TreeMap<Integer, Boolean> negativeACKWaitingList; // <NACKSequenceNumber, isRetransmission>

	private TreeMap<Integer, Boolean> seqNumbersToSend; // <SequenceNumber, isRetransmission>

	public Senderv2(Flow flow) {
		super(flow);
		hasRecvdSYNACK = false;
		hasSentFIN = false;
		timerOfTimerID = new HashMap<Integer, Timer>();
		timeToNextCycleTimerIDs = new ArrayList<Integer>();
		timerIndex = -1;
		nextCCParamsOfTimerID = new HashMap<Integer, CCParams>();
		ccParams = new CCParams(-1, -1, -1, -1);
		remainingSegments = flow.getSize();
		lastSentSeqNum = -1;
		mostRecentSequenceNum = -1;
		negativeACKWaitingList = new TreeMap<Integer, Boolean>();
		seqNumbersToSend = new TreeMap<Integer, Boolean>();

		validation = true;
		verification = true;

	}

	@Override
	public void sendFirst(Network net) {
		segmentsToSend.clear();
		segmentsToSend.add(genSYN());
	}

	@Override
	public void recvSegment(Network net, Segment segment) {
		if (!hasSentFIN) {
			switch (segment.getType()) {
			case Keywords.Segments.Types.SYN:
				segmentsToSend.clear();
				segmentsToSend.add(segment);
				break;
			case Keywords.Segments.Types.CTRL:
				timerIndex++;
				Timer timeToNextCycleTimer = new Timer(timerIndex,
						Keywords.Entities.Agents.TimerTypes.TimeToCycleTimer);
				timerOfTimerID.put(timeToNextCycleTimer.id, timeToNextCycleTimer);
				net.eventList
						.addEvent(new TimeoutEvent(Mathematics.addFloat(net.getCurrentTime(), segment.timeToNextCycle),
								srcHostID, timeToNextCycleTimer.id));
				nextCCParamsOfTimerID.put(timeToNextCycleTimer.id, new CCParams(segment.sWnd,
						segment.sInterSegmentDelay, segment.sInterval, segment.sInitialDelay));
				timeToNextCycleTimerIDs.add(timeToNextCycleTimer.id);
				break;
			case Keywords.Segments.Types.SYNACK:
				/** ===== Statistical Counters ===== **/
				flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
				/** ================================ **/
				hasRecvdSYNACK = true;
				// startSendingCycle(net);
				break;
			case Keywords.Segments.Types.ACK:
				/** ===== Statistical Counters ===== **/
				flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
				/** ================================ **/
				if (remainingSegments == 0 && negativeACKWaitingList.isEmpty()
						&& segment.getSeqNum() == lastSentSeqNum) {
					segmentsToSend.add(genFIN());
					for (int timerID : timerOfTimerID.keySet()) {
						timerOfTimerID.get(timerID).isActive = false;
					}
				}
				break;
			case Keywords.Segments.Types.NACK:
				negativeACKWaitingList.put(segment.getSeqNum(), true);
				break;
			case Keywords.Segments.Types.FINACK:
				break;
			default:
				Main.error("Senderv2", "recvSegment", "Invalid segmentType.");
				break;
			}
		}
	}

	@Override
	public void timeout(Network net, int timerID) {
		Timer currentTimer = timerOfTimerID.get(timerID);
		timerOfTimerID.remove(timerID);
		if (currentTimer.isActive) {
			switch (currentTimer.type) {
			case Keywords.Entities.Agents.TimerTypes.TimeToCycleTimer:

				for (int id : timerOfTimerID.keySet()) {
					//if (!timeToNextCycleTimerIDs.contains(id)) {
						timerOfTimerID.get(id).isActive = false;
					//}
				}
				ccParams = nextCCParamsOfTimerID.get(timerID);
				nextCCParamsOfTimerID.remove(timerID);
				if (hasRecvdSYNACK) {
					startSendingCycle(net);
				} else {
					Debugger.error("Senderv2", "timeOut", "Cycle starts before SYNACK arrival.");
				}
				break;
			case Keywords.Entities.Agents.TimerTypes.InitialDelayTimer:
				startSendingInterval(net);
				break;
			case Keywords.Entities.Agents.TimerTypes.IntervalTimer:
				startSendingInterval(net);
				break;
			case Keywords.Entities.Agents.TimerTypes.InterSegmentDelayTimer:
				sendSingleSegment(net);
				break;
			default:
				break;
			}
		}
	}

	private void startSendingCycle(Network net) {
		if (ccParams.sInitialDelay == 0) {
			startSendingInterval(net);
		} else if (ccParams.sInitialDelay > 0) {
			timerIndex++;
			Timer initialDelayTimer = new Timer(timerIndex, Keywords.Entities.Agents.TimerTypes.InitialDelayTimer);
			timerOfTimerID.put(initialDelayTimer.id, initialDelayTimer);
			net.eventList.addEvent(new TimeoutEvent(Mathematics.addFloat(net.getCurrentTime(), ccParams.sInitialDelay),
					srcHostID, initialDelayTimer.id));
		} else {
			Debugger.error("Senderv2", "startSendingCycle", "Invalid sInitialDelay (= " + ccParams.sInitialDelay + ")");
		}
	}

	private void startSendingInterval(Network net) {
		if (!hasSentFIN) {
			for (int seqNumCount = 0; seqNumCount < ccParams.sWnd; seqNumCount++) {
				if (!negativeACKWaitingList.isEmpty()) {
					int sequenceNumber = negativeACKWaitingList.firstKey();
					negativeACKWaitingList.remove(sequenceNumber);
					seqNumbersToSend.put(sequenceNumber, true);
				}
			}
			int upperLimit = (int) Mathematics.minDouble((double) ccParams.sWnd, (double) remainingSegments);
			mostRecentSequenceNum = lastSentSeqNum;
			for (int seqNumCount = 0; seqNumCount < upperLimit; seqNumCount++) {
				mostRecentSequenceNum++;
				seqNumbersToSend.put(mostRecentSequenceNum, false);
			}
			if (seqNumbersToSend.size() > 0) {
				sendSingleSegment(net);
			}
			timerIndex++;
			Timer sIntervalTimer = new Timer(timerIndex, Keywords.Entities.Agents.TimerTypes.IntervalTimer);
			timerOfTimerID.put(sIntervalTimer.id, sIntervalTimer);
			net.eventList.addEvent(new TimeoutEvent(Mathematics.addFloat(net.getCurrentTime(), ccParams.sInterval),
					srcHostID, sIntervalTimer.id));
		}
	}

	private void sendSingleSegment(Network net) {
		if (!seqNumbersToSend.isEmpty()) {
			int sequenceNumber = seqNumbersToSend.firstKey();
			lastSentSeqNum = sequenceNumber; // TODO note for the retransmissions
			segmentsToSend.add(genDATASegment(sequenceNumber, seqNumbersToSend.get(sequenceNumber)));
			seqNumbersToSend.remove(sequenceNumber);
			if (!seqNumbersToSend.isEmpty()) {
				timerIndex++;
				Timer interSegmentDelayTimer = new Timer(timerIndex,
						Keywords.Entities.Agents.TimerTypes.InterSegmentDelayTimer);
				timerOfTimerID.put(interSegmentDelayTimer.id, interSegmentDelayTimer);
				net.eventList.addEvent(
						new TimeoutEvent(Mathematics.addFloat(net.getCurrentTime(), ccParams.sInterSegmentDelay),
								srcHostID, interSegmentDelayTimer.id));
			}
		}
	}

	private Segment genSYN() {
		Segment segment = new Segment(flow.getID(), Keywords.Segments.Types.SYN,
				Keywords.Segments.SpecialSequenceNumbers.SYNSeqNum, Keywords.Segments.Sizes.SYNSegSize, srcHostID,
				dstHostID);
		lastSentSeqNum = 0;
		mostRecentSequenceNum = 0;
		return segment;
	}

	private Segment genFIN() {
		lastSentSeqNum++;
		mostRecentSequenceNum++;
		Segment segment = new Segment(flow.getID(), Keywords.Segments.Types.UncontrolledFIN, lastSentSeqNum,
				Keywords.Segments.Sizes.FINSegSize, srcHostID, dstHostID);
		hasSentFIN = true;
		return segment;
	}

	private Segment genDATASegment(int seqNumber, boolean isRetransmission) {
		if (remainingSegments > 0) {
			Segment segment = new Segment(this.flow.getID(), Keywords.Segments.Types.DATA, seqNumber,
					Keywords.Segments.Sizes.DataSegSize, srcHostID, dstHostID);
			if (!isRetransmission) {
				remainingSegments--;
			}
			return segment;
		} else if (remainingSegments == 0) {
			Debugger.error("Senderv2", "genDATASegment",
					"CASE 0: Upper bound for sending window and remaining segments is wrong.");
			Debugger.stopFlag();
			return null;
		} else {
			Debugger.error("Senderv2", "genDATASegment",
					"CASE Negative: Upper bound for sending window and remaining segments is wrong.");
			Debugger.stopFlag();
			return null;
		}

	}
}
