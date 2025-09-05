package syntacticanalysis.node;

public class BooleanNode extends ExpressionNode {
	private final String value;
	public BooleanNode(int numLine, String value) {
		super(numLine,"boolean");
		this.value = value;
	}
}
