package syntacticanalysis.node;

public class ParameterNode extends ExpressionNode {
	public final Node identifier;
	public ParameterNode(int numLine, String type, Node identifier) {
		super(numLine, type);
		this.identifier = identifier;
	}
	public String toString() {
		return type + " " + identifier;
	}
}
