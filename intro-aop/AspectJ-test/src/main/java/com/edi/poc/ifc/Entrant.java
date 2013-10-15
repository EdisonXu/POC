package com.edi.poc.ifc;

import com.edi.poc.Zoo;

public interface Entrant {

	boolean isCharged();

	public void setCharged(boolean isCharged);

	String getName();

	void visit(Zoo zoo, HALL_NAME hallName);
}
