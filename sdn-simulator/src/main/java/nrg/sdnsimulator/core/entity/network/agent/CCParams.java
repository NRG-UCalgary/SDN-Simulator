package nrg.sdnsimulator.core.entity.network.agent;

public class CCParams {
	private int sWnd;
	private float sInterSegmentDelay;
	private float sInterval;
	private float sInitialDelay;

	public CCParams(int sWnd, float sInterSegmentDelay, float sInterval, float sInitialDelay) {
		this.sWnd = sWnd;
		this.sInterSegmentDelay = sInterSegmentDelay;
		this.sInterval = sInterval;
		this.sInitialDelay = sInitialDelay;
	}

	public void update(int sWnd, float sInterSegmentDelay, float sInterval, float sInitialDelay) {
		this.sWnd = sWnd;
		this.sInterSegmentDelay = sInterSegmentDelay;
		this.sInterval = sInterval;
		this.sInitialDelay = sInitialDelay;
	}

	public int getsWnd() {
		return sWnd;
	}

	public void setsWnd(int sWnd) {
		this.sWnd = sWnd;
	}

	public float getsInterSegmentDelay() {
		return sInterSegmentDelay;
	}

	public void setsInterSegmentDelay(float sInterSegmentDelay) {
		this.sInterSegmentDelay = sInterSegmentDelay;
	}

	public float getsInterval() {
		return sInterval;
	}

	public void setsInterval(float sInterval) {
		this.sInterval = sInterval;
	}

	public float getsInitialDelay() {
		return sInitialDelay;
	}

	public void setsInitialDelay(float sInitialDelay) {
		this.sInitialDelay = sInitialDelay;
	}

}
