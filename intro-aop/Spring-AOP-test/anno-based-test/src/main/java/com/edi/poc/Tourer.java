package com.edi.poc;

import com.edi.poc.ifc.Entrant;
import com.edi.poc.ifc.HALL_NAME;

public class Tourer implements Entrant {

	private String name;
	private boolean isCharged = false;
	
	public Tourer(String name)
	{
		this.name = name;
	}
	
	public boolean isCharged() {
		return isCharged;
	}

	public void setCharged(boolean isCharged) {
		this.isCharged = isCharged;
	}

	public String getName() {
		return name;
	}
	
	public void visit(Zoo zoo, HALL_NAME hallName) {
		zoo.enter(this, hallName).visit(this);
	}

}
