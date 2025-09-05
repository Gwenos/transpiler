package syntacticanalysis.node;

public abstract class ExpressionNode extends Node {
	public String type;

	public ExpressionNode(int numLine, String type) {
		super(numLine);
		this.type = type;
	}
}
