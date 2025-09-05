package syntacticanalysis.node;

import java.util.List;

public class MethodDeclarationNode extends Node {
	private final Node modifiers;
	public final Node type;
	public final String methodName;
	public final List<Node> params;
	public final Node block;
	public MethodDeclarationNode (int numLine, Node modifiers, Node type, String methodName, List<Node> params, Node block){
		super(numLine);
		this.modifiers = modifiers;
		this.type = type;
		this.methodName = methodName;
		this.params = params;
		this.block = block;
	}
	public String toString (){
		String str = modifiers + " " + type + " " + methodName + "(";
		for (Node param : params) {
			str += param.toString();
			str += ",";
		}
		str += ")" + block;
		return str;
	}
}
