package syntacticAnalysis.node;

public class BooleanNode extends ExpressionNode {
	private final String value;
	public BooleanNode(String value) {
		super("boolean");
		this.value = value;
	}
}
