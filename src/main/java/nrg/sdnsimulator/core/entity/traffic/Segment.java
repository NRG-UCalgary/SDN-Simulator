package nrg.sdnsimulator.core.entity.traffic;

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

	public int getsWnd() {
		return sWnd;
	}

	public void setsWnd(int sWnd) {
		this.sWnd = sWnd;
	}

	public float getTimeToNextCycle() {
		return timeToNextCycle;
	}

	public void setTimeToNextCycle(float timeToNextCycle) {
		this.timeToNextCycle = timeToNextCycle;
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

	public int getBigRTT_() {
		return bigRTT_;
	}

	public void setBigRTT_(int bigRTT_) {
		this.bigRTT_ = bigRTT_;
	}

	public int getDstHostID() {
		return dstHostID;
	}

	public void setDstHostID(int dstHostID) {
		this.dstHostID = dstHostID;
	}

	public int getFlowID() {
		return flowID;
	}

	public void setFlowID(int flowID) {
		this.flowID = flowID;
	}

	public float getInterSegmentDelay_() {
		return interSegmentDelay_;
	}

	public void setInterSegmentDelay_(float interSegmentDelay_) {
		this.interSegmentDelay_ = interSegmentDelay_;
	}

	public float getRtt_() {
		return rtt_;
	}

	public void setRtt_(float rtt_) {
		this.rtt_ = rtt_;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSrcHostID() {
		return srcHostID;
	}

	public void setSrcHostID(int srcHostID) {
		this.srcHostID = srcHostID;
	}

	public int getsWnd_() {
		return sWnd_;
	}

	public void setsWnd_(int sWnd_) {
		this.sWnd_ = sWnd_;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
