package syntacticAnalysis.node;

//=======================================TYPE====================================
public class LiteralNode extends Node {

	private String regexBoolean = "(true|false)";
	private String regexString = "\\d+(\\.\\d+)?|\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^\"\\\\])*'";
	private String regexInt = "-?\\d+";
	private String regexDouble = "-?\\d+\\.\\d+";

	String value;
	String type;
	public LiteralNode(String value) {
		this.value = value;
		this.type = value.matches(regexBoolean) ? "boolean" :
						value.matches(regexString) ? "String" :
							value.matches(regexInt) ? "int" :
								value.matches(regexDouble) ? "double" : "UNKNOW";
	}
	public String toString() { return value; }
}
