package syntacticanalysis.node;

public class ReturnNode extends Node {
	public final Node expression;
	public ReturnNode(int numLine, Node expression){
		super(numLine);
		this.expression = expression;
	}
	public String toString(){return "return " + expression + ";";}
}
