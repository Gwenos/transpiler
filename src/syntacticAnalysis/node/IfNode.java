package syntacticAnalysis.node;

import java.util.List;

public class IfNode extends Node {
	public final ExpressionNode boolExpr;
	public final Node block;
	public final List<IfNode> orElse;
	public IfNode(ExpressionNode boolExpr, Node block, List<IfNode> orElse){
		this.boolExpr = boolExpr;
		this.block = block;
		this.orElse = orElse;
	}
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("if (").append(boolExpr).append(") ").append(block);
		for(IfNode ifnode : orElse){
			if(ifnode.boolExpr.toString().isEmpty()){
				str.append("else ").append(ifnode.block);
			}else {
				str.append("else if(").append(ifnode.boolExpr).append(") ").append(ifnode.block);
			}
		}
		return str.toString();
	}
}
