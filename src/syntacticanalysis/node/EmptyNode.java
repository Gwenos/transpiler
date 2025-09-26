package syntacticanalysis.node;

public class EmptyNode extends ExpressionNode {
	public EmptyNode(int numLine) {
		super(numLine, "empty");
	}

	public String toString() {return "";}
}
