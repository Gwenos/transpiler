package util;

public class Error {

	public static void lexicalError(String line, String token, int numLine) {
		System.out.println(ANSI.CYAN + "[ligne " + numLine + "]" + ANSI.RED + " Lexical error: " + ANSI.RESET + line + "\n\t>>"+token);
	}
}
