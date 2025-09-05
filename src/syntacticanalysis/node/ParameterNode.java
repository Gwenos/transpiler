package syntacticanalysis.node;

public class ParameterNode extends Node {
	public final Node type;
	public final Node identifier;
	public ParameterNode(int numLine, Node type, Node identifier) {
		super(numLine);
		this.type = type;
		this.identifier = identifier;
	}
	public String toString() {
		return type + " " + identifier;
	}
}
