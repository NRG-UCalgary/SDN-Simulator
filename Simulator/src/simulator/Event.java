package simulator;

public abstract class Event {

	protected float eventTime;

	public Event(float eventTime) {
		this.eventTime = eventTime;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract void execute(Network net);
}
