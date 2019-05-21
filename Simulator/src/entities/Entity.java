package entities;

import utilities.Logger;

public abstract class Entity {
	protected final int ID;
	protected Logger log;

	public Entity(int id) {
		this.ID = id;
		log = new Logger();
	}

	public int getID() {
		return this.ID;
	}
}
