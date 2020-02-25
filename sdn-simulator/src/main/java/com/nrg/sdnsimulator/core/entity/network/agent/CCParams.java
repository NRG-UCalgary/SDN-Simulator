package com.nrg.sdnsimulator.core.entity.network.agent;

import com.nrg.sdnsimulator.core.system.Main;

public class CCParams {
	public int sWnd;
	public float sInterSegmentDelay;
	public float sInterval;
	public float sInitialDelay;

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

	public void validate() {
		if (sWnd < 0)
			Main.error("ccParams", "validate", "Invalid value for sWnd (=" + sWnd + ")");

		if (sInterSegmentDelay < 0)
			Main.error("ccParams", "validate", "Invalid value for sInterSegmentDelay (=" + sInterSegmentDelay + ")");

		if (sInterval < 0)
			Main.error("ccParams", "validate", "Invalid value for sInterval (=" + sWnd + ")");

		if (sInitialDelay < 0)
			Main.error("ccParams", "validate", "Invalid value for sInitialDelay (=" + sInitialDelay + ")");
	}

}
