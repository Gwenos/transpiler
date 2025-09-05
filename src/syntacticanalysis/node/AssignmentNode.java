package syntacticanalysis.node;

public class AssignmentNode extends Node {
	public final String type;
	public final String identifier;
	public final Node value;
	public AssignmentNode(int numLine, String type, String identifier, Node value){
		super(numLine);
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}
	public String toString(){return type + " " + identifier + " = " + value + ";";}
}
