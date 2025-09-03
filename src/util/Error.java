package util;

public class Error {

	public static void lexicalError(String line, String token, int nbline) {
		System.out.println(ANSI.CYAN + "[ligne " + nbline + "]" + ANSI.RED + " Lexical error: " + ANSI.RESET + line + "\n\t>>"+token);
	}
}
