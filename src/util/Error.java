package util;

public class Error {

	public static void lexicalError(int numLine, String token) {
		System.out.println(ANSI.CYAN + "[ligne " + numLine + "]" + ANSI.RED + " Lexical error: " + ANSI.RESET +token);
	}

	public static void syntacticError(int numLine, String token) {
		System.out.println(ANSI.CYAN + "[ligne " + numLine + "]" + ANSI.RED + " Syntactic error: " + ANSI.RESET + token);
	}

	public static void semanticError(int numLine, String message) {
		System.out.println(ANSI.CYAN + "[ligne " + numLine + "]" + ANSI.RED + " Semantic error: " + ANSI.RESET + message);
//		throw new RuntimeException(message);
	}
}
