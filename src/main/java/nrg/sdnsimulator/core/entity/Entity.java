package nrg.sdnsimulator.core.entity;

public abstract class Entity {
	protected boolean validation;
	protected boolean verification;
	protected final int ID;

	public Entity(int id) {
		this.ID = id;
		validation = false;
		verification = false;
	}

	public int getID() {
		return this.ID;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public boolean isVerification() {
		return verification;
	}

	public void setVerification(boolean verification) {
		this.verification = verification;
	}

}
