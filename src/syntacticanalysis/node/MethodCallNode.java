package syntacticanalysis.node;

import java.util.List;

public class MethodCallNode extends ExpressionNode {
	public String identifier;
	public List<Node> args;
	public MethodCallNode(int numLine, String identifier, List<Node> args) {
		super(numLine, "fonction");
		this.identifier = identifier;
		this.args = args;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(identifier).append("(");
		for (Node n : args) {
			sb.append(n.toString()).append(",");
		}
		sb = new StringBuilder(sb.substring(0, sb.length() - 1));
		sb.append(")");
		return sb.toString();
	}
}
