package com.edi.poc;

import com.edi.poc.ifc.Entrant;
import com.edi.poc.ifc.Hall;

public class DinoHall implements Hall {

	public void open() {
		System.out.println("Dinosaur hall is opened.");
	}

	public void charge(Entrant e) {
		e.setCharged(true);
		System.out.println("Dianosaur hall charges " + e.getName() + " $2.00");
	}

	public void close() {
		System.out.println("Dinosaur hall is closed.");
	}

	public void visit(Entrant e) {
		if(!e.isCharged())
		{
			System.out.println(e.getName() + " needs to be charged first.");
			charge(e);
		}
		System.out.println(e.getName() + " visited diano hall.");
	}

}
