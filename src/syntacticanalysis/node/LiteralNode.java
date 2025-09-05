package syntacticanalysis.node;

//=======================================TYPE====================================
public class LiteralNode extends ExpressionNode {

	private String regexBoolean = "(true|false)";
	private String regexString = "\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'";
	private String regexInt = "-?\\d+";
	private String regexDouble = "-?\\d+\\.\\d+";

	public String lexeme;
	public String type;
	public LiteralNode(int numLine, String lexeme) {
		super(numLine,"Literal");
		this.lexeme = lexeme;
		this.type = lexeme.matches(regexBoolean) ? "boolean" :
						lexeme.matches(regexString) ? "String" :
							lexeme.matches(regexInt) ? "int" :
								lexeme.matches(regexDouble) ? "double" : "UNKNOW";
	}
	public String toString() { return lexeme; }
}
