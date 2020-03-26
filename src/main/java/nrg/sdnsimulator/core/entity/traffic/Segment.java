package nrg.sdnsimulator.core.entity.traffic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Segment {

	private int sWnd = 0;
	private float timeToNextCycle = 0;
	private float sInitialDelay = 0;
	private float sInterval = 0;
	private float sInterSegmentDelay = 0;
	private int bigRTT_;
	private int dstHostID;
	private int flowID;
	private float interSegmentDelay_;
	private float rtt_;
	private int seqNum;
	private int size; // in bits
	private int srcHostID;
	private int sWnd_;
	private int type;

	public Segment(int flowID, int type, int seqNum, int size, int srcHostID, int dstHostID) {
		this.flowID = flowID;
		sWnd_ = 0;
		rtt_ = 0;
		bigRTT_ = 0;
		this.type = type;
		this.seqNum = seqNum;
		this.size = size;
		this.srcHostID = srcHostID;
		this.dstHostID = dstHostID;

	}

	public void changeType(int newType) {
		type = newType;
	}

	
	//TODO: resolve the conflict
	public int getsWnd() {
		return sWnd;
	}

	public void setsWnd(int sWnd) {
		this.sWnd = sWnd;
	}

	public float getsInitialDelay() {
		return sInitialDelay;
	}

	public void setsInitialDelay(float sInitialDelay) {
		this.sInitialDelay = sInitialDelay;
	}

	public float getsInterval() {
		return sInterval;
	}

	public void setsInterval(float sInterval) {
		this.sInterval = sInterval;
	}

	public float getsInterSegmentDelay() {
		return sInterSegmentDelay;
	}

	public void setsInterSegmentDelay(float sInterSegmentDelay) {
		this.sInterSegmentDelay = sInterSegmentDelay;
	}

	public int getsWnd_() {
		return sWnd_;
	}

	public void setsWnd_(int sWnd_) {
		this.sWnd_ = sWnd_;
	}

}
