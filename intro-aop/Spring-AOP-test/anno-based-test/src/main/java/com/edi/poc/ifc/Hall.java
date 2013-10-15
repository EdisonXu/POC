package com.edi.poc.ifc;

public interface Hall {

	void open();
	void charge(Entrant e);
	void close();
	void visit(Entrant e);
}
