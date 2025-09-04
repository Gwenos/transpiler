package syntacticAnalysis.node;

public class IdentifierNode extends Node {
	String value;
	public IdentifierNode(String value) {
		this.value = value;
	}
	public String toString() { return value; }
}
