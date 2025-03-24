package com.yaksha.assignment;

import com.yaksha.utility.MathOperations;
import com.yaksha.utility.StringOperations;

public class Main {
	public static void main(String[] args) {
		// Using MathOperations class
		MathOperations mathOperations = new MathOperations();
		System.out.println("Addition of 5 and 3: " + mathOperations.add(5, 3));
		System.out.println("Multiplication of 5 and 3: " + mathOperations.multiply(5, 3));

		// Using StringOperations class
		StringOperations stringOperations = new StringOperations();
		System.out.println("Concatenation of 'Hello' and 'World': " + stringOperations.concatenate("Hello", "World"));
		System.out.println("Length of 'Hello': " + stringOperations.getLength("Hello"));
	}
}
