package de.mpg.imeji.user.util;

import java.util.Random;

public class PasswordGenerator 
{
	public String generatePassword()
	{
		String[] characters = { 
				"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "m", "n", "p", "q", "r", "s", "t", "u", "v", 
				"w", "x", "y", "z", "A", "B", "C", "D", "E", "F", 
				"G", "H", "I", "J", "K", "M", "N", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", 
				"3", "4","5", "6", "7", "8", "9"};
		
		Random random = new Random();
		String password = "";
		
		for (int i = 0; i < 6; i++) 
		{
			password += characters[random.nextInt(characters.length)];
		}
		
		return password;
	}

}
