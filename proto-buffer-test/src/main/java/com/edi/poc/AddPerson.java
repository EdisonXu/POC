package com.edi.poc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.AddressBookProtos.Person.PhoneType;

public class AddPerson {

	// This function fills in a Person message based on user input.
	static Person PromptForAddress(BufferedReader stdin, PrintStream stdout) throws IOException {
		Person.Builder person = Person.newBuilder();
		
		stdout.print("Enter person ID: ");
		person.setId(Integer.valueOf(stdin.readLine()));
		
		stdout.print("Enter name: ");
		person.setName(stdin.readLine());
		
		stdout.print("Enter email address (blank for none): ");
		String email = stdin.readLine();
		if(email.length()>0)
			person.setEmail(email);
		
		while(true){
			stdout.print("Enter a phone number(or leave blank to finish): ");
			String number = stdin.readLine();
			if(number.length()==0) break;
			Person.PhoneNumber.Builder phoneNumber = Person.PhoneNumber.newBuilder();
			phoneNumber.setNumber(number);
			
			stdout.print("Is this a mobile, home, or work phone?");
			String type = stdin.readLine();
			switch(type)
			{
			case "home":
			case "HOME":
				phoneNumber.setType(PhoneType.HOME);
				break;
			case "mobile":
			case "MOBILE":
				phoneNumber.setType(PhoneType.MOBILE);
				break;
			case "work":
			case "WORK":
				phoneNumber.setType(PhoneType.WORK);
				break;
			default: 
				stdout.println("Unknown phone type. Using default.");
			}
			
			person.addPhone(phoneNumber);
		}
		
		
		return person.build();
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length!=1){
			System.err.println("Usage: AddPerson ADDRESS_BOOK_FILE");
			System.exit(-1);
		}
		
		AddressBook.Builder addressBook = AddressBook.newBuilder();
		
		try {
			addressBook.mergeFrom(new FileInputStream(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println(args[0] + ": File not found. Create a new file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		addressBook.addPerson(PromptForAddress(new BufferedReader(new InputStreamReader(System.in)), System.out));
		
		FileOutputStream output = new FileOutputStream(args[0]);
		addressBook.build().writeTo(output);
		output.close();
	}
}
