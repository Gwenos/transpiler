package syntacticAnalysis.node;

public class WhileNode extends Node {
	public final ExpressionNode bool_expr;
	public final Node block;
	public WhileNode(ExpressionNode bool_expr, Node block){
		this.bool_expr = bool_expr;
		this.block = block;
	}
	public String toString(){return "while(" + bool_expr + ")" + block;}
}
