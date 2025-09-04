package util;

public class Error {

	public static void lexicalError(String line, String token, int numLine) {
		System.out.println(ANSI.CYAN + "[ligne " + numLine + "]" + ANSI.RED + " Lexical error: " + ANSI.RESET + line + "\n\t>>"+token);
	}

	public static void syntacticError(String line, String token, int numLine) {
		System.out.println(ANSI.CYAN + "[ligne " + numLine + "]" + ANSI.RED + " Syntactic error: " + ANSI.RESET + line + "\n\t>>"+token);
	}

	public static void semanticError(String message) {
		System.out.println(ANSI.RED + " Semantic error >> " + ANSI.RESET + message);
//		throw new RuntimeException(message);
	}
}
