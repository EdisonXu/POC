package com.edi.poc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;

public class ListPeople {

	static void print(AddressBook addressBook){
		for(Person p: addressBook.getPersonList())
		{
			System.out.println("Person ID: " + p.getId());
			System.out.println("	Name: " + p.getName());
			if(p.hasEmail())
				System.out.println("	E-mail: " + p.getEmail());
			
			for(Person.PhoneNumber num:p.getPhoneList())
			{
				switch(num.getType())
				{
				case MOBILE:
					System.out.print("	Mobile phone #: ");
					break;
				case HOME:
					System.out.print("	Home phone #: ");
					break;
				case WORK:
					System.out.print("	Work phone #: ");
					break;
					default:
				}
				System.out.println(num.getNumber());
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if(args.length!=1){
			System.err.println("Usage: ListPeople ADDRESS_BOOK_FILE");
			System.exit(-1);
		}
		
		AddressBook addressBook = AddressBook.parseFrom(new FileInputStream(args[0]));
		
		print(addressBook);
	}
}
