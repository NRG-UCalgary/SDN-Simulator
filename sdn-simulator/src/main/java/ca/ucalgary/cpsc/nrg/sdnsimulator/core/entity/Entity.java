package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity;

public abstract class Entity {
	public boolean validation;
	public boolean verification;
	protected final int ID;

	public Entity(int id) {
		this.ID = id;
		validation = false;
		verification = false;
	}

	public int getID() {
		return this.ID;
	}
}
