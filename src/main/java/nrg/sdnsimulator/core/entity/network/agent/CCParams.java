package nrg.sdnsimulator.core.entity.network.agent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
