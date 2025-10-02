package syntacticanalysis.node;

import java.util.List;

public class ArgumentNode extends Node {
	public final Node expression;
	public ArgumentNode(int numLine, TypedNode expression) {
		super(numLine);
		this.expression = expression;
	}
	public String toString() {
		return expression.toString();
	}
}
