package com.edi.poc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.edi.poc.ifc.Entrant;
import com.edi.poc.ifc.HALL_NAME;
import com.edi.poc.ifc.Hall;

@Component
public class Zoo {

	private String name;
	private Map<HALL_NAME, Hall> halls;
	
	public void init(String name)
	{
		this.name = name;
		this.halls = new HashMap<HALL_NAME, Hall>();
		//halls.put(HALL_NAME.DINOSAUR, new DinoHall());
	}
	
	public void addHall(HALL_NAME hallName, Hall hall)
	{
		this.halls.put(hallName, hall);
	}
	
	public void open()
	{
		for(Hall h:halls.values())
		{
			h.open();
		}
		System.out.println("The "+ name + " Zoo " + "is opened.");
	}
	
	public void close()
	{
		for(Hall h:halls.values())
		{
			h.close();
		}
		System.out.println("The "+ name + " Zoo " + "is closed.");
	}
	
	public Hall enter(Entrant e, HALL_NAME hallName)
	{
		this.charge(e);
		return this.halls.get(hallName);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private void charge(Entrant e)
	{
		/*if(e.isCharged())
			return;*/
		System.out.println("Charge " + e.getName() + " $1.00 for ticket.");
	}
}
