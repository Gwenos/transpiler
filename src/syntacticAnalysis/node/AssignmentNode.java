package syntacticAnalysis.node;

public class AssignmentNode extends Node {
	public final String type;
	public final String identifier;
	public final Node value;
	public AssignmentNode(String type, String identifier, Node value){
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}
	public String toString(){return type + " " + identifier + " = " + value + ";";}
}
