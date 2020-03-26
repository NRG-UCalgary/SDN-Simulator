package nrg.sdnsimulator.core.entity.traffic;

import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.entity.Entity;

@Getter
@Setter
public class Flow extends Entity {

	private int srcHostID;
	private int dstHostID;
	private int size;

	/** ========== Statistical Counters ========== **/
	private float FINSendingTime;
	private float dataSendingStartTime; // in Sender Agent
	private TreeMap<Float, Float> dataSeqNumSendingTimes; // <SeqNum, Time>
	private TreeMap<Float, Float> ackSeqNumArrivalTimes; // <SeqNum, Time>
	private float totalBufferTime; // in Buffer (when getting bufferTime)
	private int totalDroppedSegments; // in Buffer
	private int totalSentSegments; // in Sender Agent
	private float totalTransmissionTime;
	private float arrivalTime; // in Simulator
	private float completionTime; // in Receiver Agent

	/** ========================================== **/

	public Flow(int ID, int srcHostID, int dstHostID, int size, float arrivalTime) {
		super(ID);
		this.srcHostID = srcHostID;
		this.dstHostID = dstHostID;
		this.size = size;
		this.arrivalTime = arrivalTime;

		/** ========== Statistical Counters Initialization ========== **/
		dataSendingStartTime = 0;
		completionTime = 0;
		totalDroppedSegments = 0;
		totalSentSegments = 0;
		totalBufferTime = 0;
		FINSendingTime = 0;
		totalTransmissionTime = 0;

		dataSeqNumSendingTimes = new TreeMap<Float, Float>();
		ackSeqNumArrivalTimes = new TreeMap<Float, Float>();
		/** ========================================================= **/
	}

	public void updateCompletionTime(float completionTime) {
		this.completionTime = completionTime;
	}

	/*---------- Statistical counters methods ----------*/
	public void updateDataSendingStartTime(float startTime) {
		dataSendingStartTime = startTime;
	}

}
