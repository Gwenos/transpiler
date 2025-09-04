package syntacticAnalysis.node;

public class WhileNode extends Node {
	public final Node bool_expr;
	public final Node block;
	public WhileNode(Node bool_expr, Node block){
		this.bool_expr = bool_expr;
		this.block = block;
	}
	public String toString(){return "while(" + bool_expr + ")" + block;}
}
