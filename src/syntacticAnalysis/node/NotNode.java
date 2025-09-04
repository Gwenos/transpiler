package syntacticAnalysis.node;

public class NotNode extends Node {
	public final Node expression;
	public NotNode(Node expression){
		this.expression = expression;
	}
	public String toString(){return "!" + expression;}

}
