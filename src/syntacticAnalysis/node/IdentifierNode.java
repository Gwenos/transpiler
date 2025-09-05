package syntacticAnalysis.node;

public class IdentifierNode extends ExpressionNode {
	public String value;
	public IdentifierNode(String value) {
		super("Identifier");
		this.value = value;
	}
	public String toString() { return value; }
}
