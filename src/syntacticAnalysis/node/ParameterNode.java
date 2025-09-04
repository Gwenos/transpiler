package syntacticAnalysis.node;

public class ParameterNode extends Node {
	public final Node type;
	public final Node identifier;
	public ParameterNode(Node type, Node identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	public String toString() {
		return type + " " + identifier;
	}
}
