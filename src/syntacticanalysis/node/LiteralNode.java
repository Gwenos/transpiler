package syntacticanalysis.node;

import lexicalanalysis.TokenType;

//=======================================TYPE====================================
public class LiteralNode extends ExpressionNode {

	private String regexBoolean = "(true|false)";
	private String regexString = "\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'";
	private String regexInt = "-?\\d+";
	private String regexDouble = "-?\\d+\\.\\d+";

	public String lexeme;
	public LiteralNode(int numLine, String lexeme, String type) {
		super(numLine,type);
		this.lexeme = lexeme;
	}
	public String toString() { return lexeme; }
}
