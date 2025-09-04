package syntacticAnalysis.node;

public class ReturnNode extends Node {
	public final Node expression;
	public ReturnNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return "return " + expression + ";";}
}
