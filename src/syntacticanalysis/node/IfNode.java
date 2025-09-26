package syntacticanalysis.node;

import java.util.List;

public class IfNode extends Node {
	public final ExpressionNode boolExpr;
	public final Node block;
	public final List<IfNode> orElse;
	public IfNode(int numLine, ExpressionNode boolExpr, Node block, List<IfNode> orElse){
		super(numLine);
		this.boolExpr = boolExpr;
		this.block = block;
		this.orElse = orElse;
	}
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("IfNodes[ ").append(boolExpr).append("\n").append(block);
		for(IfNode ifnode : orElse){
			str.append(ifnode.boolExpr).append("\n").append(ifnode.block);
		}
		str.append("]");
		return str.toString();
	}
}
