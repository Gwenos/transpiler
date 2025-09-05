package syntacticanalysis.node;

public class IdentifierNode extends ExpressionNode {
	public String value;
	public IdentifierNode(int numLine, String value) {
		super(numLine,"Identifier");
		this.value = value;
	}

	public String toString() { return value; }
}
