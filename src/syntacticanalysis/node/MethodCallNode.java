package syntacticanalysis.node;

import java.util.List;

public class MethodCallNode extends TypedNode {
	public String identifier;
	public List<Node> args;
	public MethodCallNode(int numLine, String identifier, List<Node> args) {
		super(numLine, "fonction");
		this.identifier = identifier;
		this.args = args;
	}
	public String toString() {
		return identifier + "(" + args + ")";
	}
}
